//package com.patient_porta.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.patient_porta.config.JwtAuthenticationFilter;
//import com.patient_porta.dto.admin.AdminRoleUpdateRequest;
//import com.patient_porta.dto.admin.AdminUserCreateRequest;
//import com.patient_porta.dto.admin.AdminUserDTO;
//import com.patient_porta.service.AdminUserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(
//        controllers = AdminUserController.class,
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE,
//                classes = JwtAuthenticationFilter.class
//        )
//)
//@AutoConfigureMockMvc(addFilters = true)
//@Import(TestSecurityConfig.class)
//class AdminUserControllerTest {
//
//    @Autowired MockMvc mvc;
//    @Autowired ObjectMapper om;
//
//    @MockitoBean AdminUserService service;
//
//    private AdminUserDTO dto(long id, String username, String role, String status) {
//        AdminUserDTO d = new AdminUserDTO();
//        d.setId(id);
//        d.setUsername(username);
//        d.setEmail(username + "@mail.com");
//        d.setPhone("0999");
//        d.setRole(role);
//        d.setStatus(status);
//        return d;
//    }
//
//    @Test
//    void list_unauthorized_whenNoUser() throws Exception {
//        mvc.perform(get("/api/admin/users")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized()); // 401
//        verifyNoInteractions(service);
//    }
//
//    @Test
//    @WithMockUser(roles = "PATIENT")
//    void list_forbidden_whenWrongRole() throws Exception {
//        mvc.perform(get("/api/admin/users")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden()); // 403
//        verifyNoInteractions(service);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void list_ok_whenAdmin() throws Exception {
//        when(service.listUsers()).thenReturn(List.of(
//                dto(1, "admin1", "ADMIN", "ACTIVE")
//        ));
//
//        mvc.perform(get("/api/admin/users")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].username").value("admin1"))
//                .andExpect(jsonPath("$[0].role").value("ADMIN"))
//                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
//
//        verify(service).listUsers();
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void create_ok_whenAdmin() throws Exception {
//        AdminUserCreateRequest req = new AdminUserCreateRequest();
//        req.setUsername("u1");
//        req.setEmail("u1@mail.com");
//        req.setPhone("0999");
//        req.setPassword("123456");
//        req.setRole("DOCTOR");
//
//        AdminUserDTO out = dto(10, "u1", "DOCTOR", "ACTIVE");
//        when(service.create(any(AdminUserCreateRequest.class))).thenReturn(out);
//
//        mvc.perform(post("/api/admin/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(10))
//                .andExpect(jsonPath("$.username").value("u1"))
//                .andExpect(jsonPath("$.role").value("DOCTOR"))
//                .andExpect(jsonPath("$.status").value("ACTIVE"));
//
//        verify(service).create(any(AdminUserCreateRequest.class));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void changeRole_ok_whenAdmin() throws Exception {
//        AdminRoleUpdateRequest req = new AdminRoleUpdateRequest();
//        req.setRole("PATIENT");
//
//        AdminUserDTO out = dto(5, "u5", "PATIENT", "ACTIVE");
//        when(service.changeRole(eq(5L), any(AdminRoleUpdateRequest.class))).thenReturn(out);
//
//        mvc.perform(put("/api/admin/users/5/role")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(om.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(5))
//                .andExpect(jsonPath("$.role").value("PATIENT"));
//
//        verify(service).changeRole(eq(5L), any(AdminRoleUpdateRequest.class));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void lock_ok_whenAdmin() throws Exception {
//        AdminUserDTO out = dto(7, "u7", "DOCTOR", "LOCKED");
//        when(service.lock(7L)).thenReturn(out);
//
//        mvc.perform(put("/api/admin/users/7/lock")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(7))
//                .andExpect(jsonPath("$.status").value("LOCKED"));
//
//        verify(service).lock(7L);
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void unlock_ok_whenAdmin() throws Exception {
//        AdminUserDTO out = dto(7, "u7", "DOCTOR", "ACTIVE");
//        when(service.unlock(7L)).thenReturn(out);
//
//        mvc.perform(put("/api/admin/users/7/unlock")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(7))
//                .andExpect(jsonPath("$.status").value("ACTIVE"));
//
//        verify(service).unlock(7L);
//    }
//}
