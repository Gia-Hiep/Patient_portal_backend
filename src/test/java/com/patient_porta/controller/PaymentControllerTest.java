package com.patient_porta.controller;

import com.patient_porta.dto.PaymentResultDTO;
import com.patient_porta.service.BillingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock private BillingService billingService;

    @InjectMocks
    private PaymentController controller;

    @Test
    @DisplayName("POST /api/payment/pay – trả clientSecret (REQUIRES_CONFIRMATION)")
    void pay_returnsClientSecret() {
        // Arrange
        PaymentResultDTO out = new PaymentResultDTO();
        out.setOutcome("REQUIRES_CONFIRMATION");
        out.setMessage("sec_123");
        when(billingService.createIntent(1L)).thenReturn(out);

        PaymentController.PayRequest body = new PaymentController.PayRequest();
        body.setInvoiceId(1L);

        // Act
        ResponseEntity<PaymentResultDTO> res = controller.pay(body);

        // Assert
        assertEquals(200, res.getStatusCode().value());
        assertEquals("REQUIRES_CONFIRMATION", res.getBody().getOutcome());
        assertEquals("sec_123", res.getBody().getMessage());
        verify(billingService).createIntent(1L);
    }

    @Test
    @DisplayName("POST /api/payment/confirm – trả SUCCESS khi xác minh thành công")
    void confirm_returnsSuccess() {
        // Arrange
        PaymentResultDTO out = new PaymentResultDTO();
        out.setOutcome("SUCCESS");
        out.setNewStatus("PAID");
        when(billingService.confirmPaid(1L, "pi_abc")).thenReturn(out);

        PaymentController.ConfirmRequest body = new PaymentController.ConfirmRequest();
        body.setInvoiceId(1L);
        body.setPaymentIntentId("pi_abc");

        // Act
        ResponseEntity<PaymentResultDTO> res = controller.confirm(body);

        // Assert
        assertEquals(200, res.getStatusCode().value());
        assertEquals("SUCCESS", res.getBody().getOutcome());
        assertEquals("PAID", res.getBody().getNewStatus());
        verify(billingService).confirmPaid(1L, "pi_abc");
    }
}
