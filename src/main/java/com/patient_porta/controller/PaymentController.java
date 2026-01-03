package com.patient_porta.controller;

import com.patient_porta.dto.PaymentResultDTO;
import com.patient_porta.service.BillingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final BillingService billingService;

    @PostMapping("/pay")  // tạo PaymentIntent -> trả clientSecret
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PaymentResultDTO> pay(@RequestBody PayRequest req) {
        return ResponseEntity.ok(billingService.createIntent(req.getInvoiceId()));
    }

    @PostMapping("/confirm")  // FE gọi sau khi Elements confirm xong
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PaymentResultDTO> confirm(@RequestBody ConfirmRequest req) {
        return ResponseEntity.ok(billingService.confirmPaid(req.getInvoiceId(), req.getPaymentIntentId()));
    }

    @Data public static class PayRequest { private Long invoiceId; }
    @Data public static class ConfirmRequest { private Long invoiceId; private String paymentIntentId; }
}
