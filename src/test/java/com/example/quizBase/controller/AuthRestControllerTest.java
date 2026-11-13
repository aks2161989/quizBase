package com.example.quizBase.controller;

import com.example.quizBase.dto.AuthResponse;
import com.example.quizBase.dto.RegisterRequest;
import com.example.quizBase.models.Role;
import com.example.quizBase.models.User;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthRestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthRestController authRestController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setFull_name("Test User");
        testUser.setRole(Role.ROLE_INSTRUCTOR);
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        when(userService.authenticate("testuser", "password123")).thenReturn(testUser);
        when(jwtService.generateToken("testuser")).thenReturn("mock-jwt-token");

        ResponseEntity<?> response = authRestController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthResponse);

        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("mock-jwt-token", authResponse.getToken());
        assertEquals("testuser", authResponse.getUsername());
        assertEquals("ROLE_INSTRUCTOR", authResponse.getRole());
    }

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        when(userService.authenticate("testuser", "wrongpassword"))
                .thenThrow(new IllegalArgumentException("Invalid password"));

        ResponseEntity<?> response = authRestController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
    }

    @Test
    void testLogin_MissingCredentials() {
        LoginRequest request = new LoginRequest();

        ResponseEntity<?> response = authRestController.login(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password123", "New User", "ROLE_STUDENT");
        doNothing().when(userService).registerUser(any(User.class));
        when(jwtService.generateToken("newuser")).thenReturn("mock-jwt-token-new");

        ResponseEntity<?> response = authRestController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("newuser", authResponse.getUsername());
        assertEquals("ROLE_STUDENT", authResponse.getRole());
    }

    @Test
    void testRegister_DuplicateUser_ThrowsException() {
        RegisterRequest request = new RegisterRequest("existinguser", "exist@example.com", "password123", "Exist User", "ROLE_STUDENT");
        doThrow(new IllegalArgumentException("Username is already taken"))
                .when(userService).registerUser(any(User.class));

        ResponseEntity<?> response = authRestController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
