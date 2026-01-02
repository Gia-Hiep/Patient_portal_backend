package com.patient_porta.controller;

import com.patient_porta.dto.InvoiceDetailDTO;
import com.patient_porta.dto.InvoiceSummaryDTO;
import com.patient_porta.service.BillingService;
import com.patient_porta.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;
    private final StripeService stripeService; // optional

    @GetMapping("/invoices")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<InvoiceSummaryDTO>> myInvoices() {
        return ResponseEntity.ok(billingService.listMine());
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<InvoiceDetailDTO> invoiceDetail(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getDetail(id));
    }

    // (Tuỳ chọn) tạo PaymentIntent Stripe
    @PostMapping("/invoices/{id}/create-intent")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> createIntent(@PathVariable Long id) throws Exception {
        var detail = billingService.getDetail(id);
        long amountVnd = detail.getTotalAmount().longValue();
        String clientSecret = stripeService.createPaymentIntent(amountVnd);
        return ResponseEntity.ok(java.util.Map.of("clientSecret", clientSecret));
    }


}