//package com.patient_porta.service;
//
//import com.patient_porta.dto.admin.AdminRoleUpdateRequest;
//import com.patient_porta.dto.admin.AdminUserCreateRequest;
//import com.patient_porta.entity.User;
//import com.patient_porta.repository.UserRepository;
//import org.junit.jupiter.api.*;
//import org.mockito.*;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AdminUserServiceTest {
//
//    @Mock UserRepository userRepo;
//    @Mock PasswordEncoder passwordEncoder;
//
//    @InjectMocks AdminUserService service;
//
//    @BeforeEach
//    void init() {
//        MockitoAnnotations.openMocks(this);
//        SecurityContextHolder.clearContext();
//    }
//
//    private void authAs(String username) {
//        var auth = new UsernamePasswordAuthenticationToken(username, "N/A", List.of());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//
//    private User mkUser(Long id, String username, User.Role role, User.Status status) {
//        User u = new User();
//        u.setId(id);
//        u.setUsername(username);
//        u.setEmail(username + "@mail.com");
//        u.setPhone("000");
//        u.setRole(role);
//        u.setStatus(status);
//        u.setPassword_hash("hash");
//        return u;
//    }
//
//    @Test
//    void listUsers_unauthorized_whenMeNotFound() {
//        authAs("ghost");
//        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());
//
//        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.listUsers());
//        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
//    }
//
//    @Test
//    void listUsers_forbidden_whenNotAdmin() {
//        authAs("p1");
//        when(userRepo.findByUsername("p1")).thenReturn(Optional.of(mkUser(1L, "p1", User.Role.PATIENT, User.Status.ACTIVE)));
//
//        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.listUsers());
//        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
//    }
//
//    @Test
//    void listUsers_ok_whenAdmin() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        when(userRepo.findAll()).thenReturn(List.of(
//                mkUser(1L, "u1", User.Role.DOCTOR, User.Status.ACTIVE),
//                mkUser(2L, "u2", User.Role.PATIENT, User.Status.LOCKED)
//        ));
//
//        var out = service.listUsers();
//        assertEquals(2, out.size());
//        assertEquals("u1", out.get(0).getUsername());
//    }
//
//    @Test
//    void create_badRequest_whenEmailExists() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        AdminUserCreateRequest req = new AdminUserCreateRequest();
//        req.setUsername("newu");
//        req.setEmail("dup@mail.com");
//        req.setPhone("1");
//        req.setPassword("123");
//        req.setRole("DOCTOR");
//
//        when(userRepo.findByEmail("dup@mail.com")).thenReturn(Optional.of(mkUser(1L, "x", User.Role.PATIENT, User.Status.ACTIVE)));
//
//        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(req));
//        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
//    }
//
//    @Test
//    void create_badRequest_whenUsernameExists() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        AdminUserCreateRequest req = new AdminUserCreateRequest();
//        req.setUsername("dup");
//        req.setEmail("e@mail.com");
//        req.setPhone("1");
//        req.setPassword("123");
//        req.setRole("DOCTOR");
//
//        when(userRepo.findByEmail("e@mail.com")).thenReturn(Optional.empty());
//        when(userRepo.findByUsername("dup")).thenReturn(Optional.of(mkUser(2L, "dup", User.Role.PATIENT, User.Status.ACTIVE)));
//
//        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(req));
//        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
//    }
//
//    @Test
//    void create_ok_whenAdmin() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        AdminUserCreateRequest req = new AdminUserCreateRequest();
//        req.setUsername("newu");
//        req.setEmail("newu@mail.com");
//        req.setPhone("090");
//        req.setPassword("123");
//        req.setRole("DOCTOR");
//
//        when(userRepo.findByEmail("newu@mail.com")).thenReturn(Optional.empty());
//        when(userRepo.findByUsername("newu")).thenReturn(Optional.empty());
//        when(passwordEncoder.encode("123")).thenReturn("ENC");
//
//        // capture user save
//        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
//            User u = inv.getArgument(0);
//            u.setId(100L);
//            return u;
//        });
//
//        var out = service.create(req);
//        assertEquals(100L, out.getId());
//        assertEquals("DOCTOR", out.getRole());
//        assertEquals("ACTIVE", out.getStatus());
//
//        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
//        verify(userRepo).save(captor.capture());
//        User saved = captor.getValue();
//        assertEquals("ENC", saved.getPassword_hash());
//        assertEquals(User.Role.DOCTOR, saved.getRole());
//        assertEquals(User.Status.ACTIVE, saved.getStatus());
//    }
//
//    @Test
//    void changeRole_notFound_whenUserMissing() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//        when(userRepo.findById(5L)).thenReturn(Optional.empty());
//
//        AdminRoleUpdateRequest req = new AdminRoleUpdateRequest();
//        req.setRole("PATIENT");
//
//        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.changeRole(5L, req));
//        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
//    }
//
//    @Test
//    void changeRole_ok() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        User target = mkUser(5L, "u5", User.Role.DOCTOR, User.Status.ACTIVE);
//        when(userRepo.findById(5L)).thenReturn(Optional.of(target));
//        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        AdminRoleUpdateRequest req = new AdminRoleUpdateRequest();
//        req.setRole("PATIENT");
//
//        var out = service.changeRole(5L, req);
//        assertEquals("PATIENT", out.getRole());
//    }
//
//    @Test
//    void lock_ok() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        User target = mkUser(7L, "u7", User.Role.PATIENT, User.Status.ACTIVE);
//        when(userRepo.findById(7L)).thenReturn(Optional.of(target));
//        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        var out = service.lock(7L);
//        assertEquals("LOCKED", out.getStatus());
//    }
//
//    @Test
//    void unlock_ok() {
//        authAs("admin");
//        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(mkUser(9L, "admin", User.Role.ADMIN, User.Status.ACTIVE)));
//
//        User target = mkUser(7L, "u7", User.Role.PATIENT, User.Status.LOCKED);
//        when(userRepo.findById(7L)).thenReturn(Optional.of(target));
//        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        var out = service.unlock(7L);
//        assertEquals("ACTIVE", out.getStatus());
//    }
//}
