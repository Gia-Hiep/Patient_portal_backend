// src/main/java/com/patient_porta/service/BillingService.java
package com.patient_porta.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patient_porta.dto.*;
import com.patient_porta.entity.Document;
import com.patient_porta.entity.Invoice;
import com.patient_porta.entity.User;
import com.patient_porta.repository.DocumentRepository;
import com.patient_porta.repository.InvoiceRepository;
import com.patient_porta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final InvoiceRepository invoiceRepo;
    private final DocumentRepository docRepo;
    private final UserRepository userRepo;
    private final ObjectMapper om = new ObjectMapper();
    private final StripeService stripeService;

    private User me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    public List<InvoiceSummaryDTO> listMine() {
        User u = me();
        if (u.getRole() != User.Role.PATIENT)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ bệnh nhân được xem hóa đơn của mình");
        return invoiceRepo.findByPatientIdOrderByIssueDateDesc(u.getId())
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    public InvoiceDetailDTO getDetail(Long id) {
        User u = me();
        Invoice inv = invoiceRepo.findByIdAndPatientId(id, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Truy cập bị từ chối"));
        return toDetail(inv);
    }

    /** Tạo PaymentIntent thật trên Stripe và trả clientSecret cho FE */
    public PaymentResultDTO createIntent(Long invoiceId) {
        User u = me();
        Invoice inv = invoiceRepo.findByIdAndPatientId(invoiceId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Truy cập bị từ chối"));

        if (inv.getStatus() == Invoice.Status.PAID) {
            var dto = new PaymentResultDTO();
            dto.setOutcome("ALREADY_PAID");
            dto.setMessage("Hóa đơn đã thanh toán");
            dto.setNewStatus("PAID");
            return dto;
        }
        try {
            String clientSecret = stripeService.createPaymentIntent(inv.getTotalAmount().longValue());
            var dto = new PaymentResultDTO();
            dto.setOutcome("REQUIRES_CONFIRMATION"); // FE cần confirm bằng Stripe Elements
            dto.setMessage(clientSecret);            // tạm nhét clientSecret vào message
            dto.setNewStatus("UNPAID");
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không tạo được PaymentIntent: " + e.getMessage());
        }
    }

    /** Xác minh PaymentIntent với Stripe, nếu succeeded -> cập nhật invoice = PAID */
    @Transactional
    public PaymentResultDTO confirmPaid(Long invoiceId, String paymentIntentId) {
        User u = me();
        Invoice inv = invoiceRepo.findByIdAndPatientId(invoiceId, u.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Truy cập bị từ chối"));
        try {
            var pi = stripeService.retrieve(paymentIntentId);
            if ("succeeded".equalsIgnoreCase(pi.getStatus())) {
                inv.setStatus(Invoice.Status.PAID);
                invoiceRepo.save(inv);
                var dto = new PaymentResultDTO();
                dto.setOutcome("SUCCESS");
                dto.setMessage("Thanh toán thành công");
                dto.setNewStatus("PAID");
                return dto;
            }
            var dto = new PaymentResultDTO();
            dto.setOutcome("FAIL");
            dto.setMessage("Thanh toán chưa thành công (status=" + pi.getStatus() + ")");
            dto.setNewStatus(inv.getStatus().name());
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không xác minh được thanh toán: " + e.getMessage());
        }
    }

    // ----- mapping helpers -----
    private InvoiceSummaryDTO toSummary(Invoice i) {
        var d = new InvoiceSummaryDTO();
        d.setId(i.getId()); d.setInvoiceNo(i.getInvoiceNo());
        d.setIssueDate(i.getIssueDate()); d.setTotalAmount(i.getTotalAmount());
        d.setStatus(i.getStatus().name());
        return d;
    }
    private InvoiceDetailDTO toDetail(Invoice i) {
        var d = new InvoiceDetailDTO();
        d.setId(i.getId()); d.setInvoiceNo(i.getInvoiceNo());
        d.setIssueDate(i.getIssueDate()); d.setTotalAmount(i.getTotalAmount());
        d.setStatus(i.getStatus().name());
        try {
            d.setItems(om.readValue(i.getItemsJson(), new TypeReference<>(){}));
        } catch (Exception ignore) { d.setItems(List.of()); }

        // gắn PDF INVOICE nếu có
        docRepo.findAll().stream()
                .filter(x -> x.getDocType() == Document.DocType.INVOICE)
                .filter(x -> Objects.equals(
                        x.getAppointment() != null ? x.getAppointment().getId() : null,
                        i.getAppointment() != null ? i.getAppointment().getId() : null))
                .findFirst()
                .ifPresent(x -> {
                    d.setDocumentId(x.getId());
                    d.setViewUrl("/api/documents/" + x.getId() + "/view");
                    d.setDownloadUrl("/api/documents/" + x.getId() + "/download");
                });
        return d;
    }
}
