package com.patient_porta.service;

import com.patient_porta.dto.InvoiceDetailDTO;
import com.patient_porta.dto.PaymentResultDTO;
import com.patient_porta.entity.Document;
import com.patient_porta.entity.Invoice;
import com.patient_porta.entity.User;
import com.patient_porta.repository.DocumentRepository;
import com.patient_porta.repository.InvoiceRepository;
import com.patient_porta.repository.UserRepository;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock private InvoiceRepository invoiceRepo;
    @Mock private DocumentRepository docRepo;
    @Mock private UserRepository userRepo;
    @Mock private StripeService stripeService;

    @InjectMocks
    private BillingService billingService;

    private User patient;

    @BeforeEach
    void setup() {
        patient = new User();
        patient.setId(13L);
        patient.setUsername("patient01");
        patient.setRole(User.Role.PATIENT);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("patient01", "x"));
        when(userRepo.findByUsername("patient01")).thenReturn(Optional.of(patient));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Invoice unpaid() {
        Invoice inv = new Invoice();
        inv.setId(1L);
        inv.setPatientId(13L);
        inv.setInvoiceNo("INV-001");
        inv.setIssueDate(LocalDate.now());
        inv.setTotalAmount(BigDecimal.valueOf(350_000));
        inv.setStatus(Invoice.Status.UNPAID);
        inv.setItemsJson(""" 
            [{"code":"CONSULT","name":"Khám tổng quát","qty":1,"price":150000}]
        """);
        return inv;
    }

    private Invoice paid() {
        Invoice inv = unpaid();
        inv.setId(2L);
        inv.setStatus(Invoice.Status.PAID);
        return inv;
    }

    @Test
    @DisplayName("listMine() – trả về danh sách hóa đơn của chính bệnh nhân")
    void listMine_ok() {
        when(invoiceRepo.findByPatientIdOrderByIssueDateDesc(13L))
                .thenReturn(List.of(unpaid(), paid()));

        var out = billingService.listMine();

        assertEquals(2, out.size());
        assertEquals("INV-001", out.get(0).getInvoiceNo());
        verify(invoiceRepo).findByPatientIdOrderByIssueDateDesc(13L);
    }

    @Test
    @DisplayName("getDetail() – map items & gắn link PDF INVOICE nếu có")
    void getDetail_ok_withInvoicePdf() {
        when(invoiceRepo.findByIdAndPatientId(1L, 13L)).thenReturn(Optional.of(unpaid()));

        Document doc = new Document();
        doc.setId(99L);
        doc.setDocType(Document.DocType.INVOICE);
        when(docRepo.findAll()).thenReturn(List.of(doc));

        InvoiceDetailDTO dto = billingService.getDetail(1L);

        assertEquals("INV-001", dto.getInvoiceNo());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());
        assertEquals(99L, dto.getDocumentId());
        assertTrue(dto.getViewUrl().endsWith("/api/documents/99/view"));
    }

    @Test
    @DisplayName("createIntent() – hóa đơn đã thanh toán -> trả ALREADY_PAID")
    void createIntent_alreadyPaid() {
        when(invoiceRepo.findByIdAndPatientId(2L, 13L)).thenReturn(Optional.of(paid()));

        PaymentResultDTO dto = billingService.createIntent(2L);

        assertEquals("ALREADY_PAID", dto.getOutcome());
        assertEquals("PAID", dto.getNewStatus());
        verifyNoInteractions(stripeService);
    }

    @Test
    @DisplayName("createIntent() – gọi Stripe tạo PaymentIntent và trả clientSecret")
    void createIntent_callsStripe() throws Exception {
        when(invoiceRepo.findByIdAndPatientId(1L, 13L)).thenReturn(Optional.of(unpaid()));
        when(stripeService.createPaymentIntent(350_000L)).thenReturn("sec_123");

        PaymentResultDTO dto = billingService.createIntent(1L);

        assertEquals("REQUIRES_CONFIRMATION", dto.getOutcome());
        assertEquals("sec_123", dto.getMessage());
        verify(stripeService).createPaymentIntent(350_000L);
    }

    @Test
    @DisplayName("confirmPaid() – PaymentIntent succeeded -> cập nhật PAID")
    void confirmPaid_success() throws Exception {
        var inv = unpaid();
        when(invoiceRepo.findByIdAndPatientId(1L, 13L)).thenReturn(Optional.of(inv));

        PaymentIntent pi = mock(PaymentIntent.class);
        when(pi.getStatus()).thenReturn("succeeded");
        when(stripeService.retrieve("pi_123")).thenReturn(pi);

        PaymentResultDTO dto = billingService.confirmPaid(1L, "pi_123");

        assertEquals("SUCCESS", dto.getOutcome());
        assertEquals("PAID", dto.getNewStatus());
        verify(invoiceRepo).save(argThat(i -> i.getStatus() == Invoice.Status.PAID));
    }

    @Test
    @DisplayName("confirmPaid() – PaymentIntent chưa succeeded -> FAIL và không đổi trạng thái")
    void confirmPaid_notSucceeded() throws Exception {
        var inv = unpaid();
        when(invoiceRepo.findByIdAndPatientId(1L, 13L)).thenReturn(Optional.of(inv));

        PaymentIntent pi = mock(PaymentIntent.class);
        when(pi.getStatus()).thenReturn("processing");
        when(stripeService.retrieve("pi_pending")).thenReturn(pi);

        PaymentResultDTO dto = billingService.confirmPaid(1L, "pi_pending");

        assertEquals("FAIL", dto.getOutcome());
        assertEquals("UNPAID", dto.getNewStatus());
        verify(invoiceRepo, never()).save(any());
    }
}
