package com.patient_porta.service;

import com.patient_porta.entity.Document;
import com.patient_porta.entity.PatientProfile;
import com.patient_porta.entity.User;
import com.patient_porta.repository.DocumentRepository;
import com.patient_porta.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository docRepo;
    @Mock private UserRepository userRepo;

    @InjectMocks
    private DocumentService documentService;

    private Path root;

    private User patient() {
        User u = new User();
        u.setId(13L);
        u.setUsername("patient01");
        u.setRole(User.Role.PATIENT);
        return u;
    }

    private Document invDoc(String path) {
        Document d = new Document();
        d.setId(5L);
        d.setDocType(Document.DocType.INVOICE);
        d.setFilePath(path);
        d.setTitle("HoaDon-INV-001");
        var u = patient(); // trả về User
        PatientProfile pf = new PatientProfile();
        pf.setUser(u);
        d.setPatient(pf);

        return d;
    }

    @BeforeEach
    void setup() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("patient01", "x"));
        when(userRepo.findByUsername("patient01")).thenReturn(Optional.of(patient()));

        root = Files.createTempDirectory("files-root-");
        ReflectionTestUtils.setField(documentService, "storageRoot", root.toString());
        Files.createDirectories(root.resolve("uploads/invoices"));
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        if (root != null) {
            Files.walk(root)
                    .sorted((a,b) -> b.getNameCount() - a.getNameCount())
                    .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignore) {} });
        }
    }

    @Test
    @DisplayName("streamPdf(view) – inline + tồn tại file")
    void streamPdf_inline_ok() throws Exception {
        // Arrange: có file
        Path file = root.resolve("uploads/invoices/invoice-001.pdf");
        Files.writeString(file, "dummy pdf");
        when(docRepo.findByIdAndPatient_UserId(5L, 13L))
                .thenReturn(Optional.of(invDoc("/uploads/invoices/invoice-001.pdf")));

        // Act
        ResponseEntity<Resource> res = documentService.streamPdf(5L, false);

        // Assert
        assertTrue(res.getHeaders().getFirst("Content-Disposition").startsWith("inline"));
        assertNotNull(res.getBody());
    }

    @Test
    @DisplayName("streamPdf(download) – attachment header")
    void streamPdf_download_ok() throws Exception {
        Path file = root.resolve("uploads/invoices/invoice-002.pdf");
        Files.writeString(file, "dummy pdf 2");
        when(docRepo.findByIdAndPatient_UserId(5L, 13L))
                .thenReturn(Optional.of(invDoc("files/invoices/invoice-002.pdf"))); // sẽ normalize

        ResponseEntity<Resource> res = documentService.streamPdf(5L, true);

        assertTrue(res.getHeaders().getFirst("Content-Disposition").startsWith("attachment"));
    }

    @Test
    @DisplayName("streamPdf – không có file -> 404")
    void streamPdf_missingFile_404() {
        when(docRepo.findByIdAndPatient_UserId(5L, 13L))
                .thenReturn(Optional.of(invDoc("/uploads/invoices/missing.pdf")));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> documentService.streamPdf(5L, false));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("streamPdf – user không phải bệnh nhân -> 403")
    void streamPdf_notPatient_403() {
        User doctor = new User();
        doctor.setId(77L);
        doctor.setUsername("doctor01");
        doctor.setRole(User.Role.DOCTOR);
        when(userRepo.findByUsername("patient01")).thenReturn(Optional.of(doctor));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> documentService.streamPdf(5L, false));
        assertEquals(403, ex.getStatusCode().value());
        verify(docRepo, never()).findByIdAndPatient_UserId(any(), any());
    }
}
