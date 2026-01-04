package com.patient_porta.service;

import com.patient_porta.dto.admin.MedicalServiceDTO;
import com.patient_porta.dto.MedicalServiceUpsertRequest;
import com.patient_porta.entity.MedicalService;
import com.patient_porta.repository.MedicalServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MedicalServiceServiceTest {

    @Mock
    private MedicalServiceRepository repo;

    @InjectMocks
    private MedicalServiceService service;

    private MedicalService activeService;
    private MedicalService inactiveService;

    @BeforeEach
    void setup() {
        activeService = new MedicalService();
        activeService.setId(1L);
        activeService.setCode("CONSULT");
        activeService.setName("Khám tư vấn");
        activeService.setDescription("Khám tư vấn tổng quát");
        activeService.setPrice(new BigDecimal("150000"));
        activeService.setActive(true);

        inactiveService = new MedicalService();
        inactiveService.setId(2L);
        inactiveService.setCode("LAB_BLOOD");
        inactiveService.setName("Xét nghiệm máu");
        inactiveService.setDescription(null);
        inactiveService.setPrice(new BigDecimal("200000"));
        inactiveService.setActive(false);
    }

    @Test
    void adminListAll_shouldReturnAll() {
        when(repo.findAll()).thenReturn(List.of(activeService, inactiveService));

        List<MedicalServiceDTO> res = service.adminListAll();

        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(x -> x.getId().equals(1L)));
        assertTrue(res.stream().anyMatch(x -> x.getId().equals(2L)));
        verify(repo).findAll();
    }

    @Test
    void listActive_shouldReturnOnlyActive() {
        when(repo.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(activeService));

        List<MedicalServiceDTO> res = service.listActive();

        assertEquals(1, res.size());
        assertEquals(1L, res.get(0).getId());
        assertTrue(res.get(0).isActive());
        verify(repo).findByActiveTrueOrderByNameAsc();
    }

    @Test
    void adminCreate_validPrice_shouldCreateAndReturnDto() {
        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("Chụp X-quang");
        req.setDescription("Chụp X-quang trong cơ thể");
        req.setPrice(new BigDecimal("350000"));

        // code random => mock existsByCode để luôn cho là chưa tồn tại
        when(repo.existsByCode(anyString())).thenReturn(false);

        when(repo.save(any(MedicalService.class))).thenAnswer(inv -> {
            MedicalService e = inv.getArgument(0);
            e.setId(10L); // giả lập DB sinh id
            // code được generate trong service
            return e;
        });

        MedicalServiceDTO dto = service.adminCreate(req);

        assertEquals(10L, dto.getId());
        assertEquals("Chụp X-quang", dto.getName());
        assertEquals(new BigDecimal("350000"), dto.getPrice());
        assertTrue(dto.isActive());
        assertNotNull(dto.getCode());
        verify(repo).save(any(MedicalService.class));
    }

    @Test
    void adminCreate_invalidPrice_shouldThrow400_withMessage() {
        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("Khám nội");
        req.setDescription("...");
        req.setPrice(new BigDecimal("-1"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.adminCreate(req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason() != null && ex.getReason().contains("Giá không hợp lệ."));
        verify(repo, never()).save(any());
    }

    @Test
    void adminUpdate_notFound_shouldThrow404() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("ABC");
        req.setDescription("DEF");
        req.setPrice(new BigDecimal("1"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.adminUpdate(999L, req));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason() != null && ex.getReason().contains("Không tìm thấy dịch vụ"));
        verify(repo).findById(999L);
        verify(repo, never()).save(any());
    }

    @Test
    void adminUpdate_invalidPrice_shouldThrow400() {
        when(repo.findById(1L)).thenReturn(Optional.of(activeService));

        MedicalServiceUpsertRequest req = new MedicalServiceUpsertRequest();
        req.setName("Khám tư vấn");
        req.setDescription("Update");
        req.setPrice(new BigDecimal("0"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.adminUpdate(1L, req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason() != null && ex.getReason().contains("Giá không hợp lệ."));
        verify(repo).findById(1L);
        verify(repo, never()).save(any());
    }

    @Test
    void adminDelete_shouldSoftDelete_setActiveFalse_andSave() {
        when(repo.findById(1L)).thenReturn(Optional.of(activeService));
        when(repo.save(any(MedicalService.class))).thenAnswer(inv -> inv.getArgument(0));

        // method của bạn là void
        service.adminDelete(1L);

        verify(repo).findById(1L);
        verify(repo).save(argThat(s -> s.getId().equals(1L) && !s.isActive()));
    }
}
