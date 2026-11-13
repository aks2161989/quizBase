package com.example.quizBase.service;

import com.example.quizBase.dao.UserRepository;
import com.example.quizBase.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testRegisterUser_EncodesPassword() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setFull_name("New User");
        user.setPassword("rawPassword123");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        userService.registerUser(user);

        assertNotEquals("rawPassword123", user.getPassword());
        assertTrue(passwordEncoder.matches("rawPassword123", user.getPassword()));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterUser_DuplicateUsername_ThrowsException() {
        User user = new User();
        user.setUsername("existinguser");
        user.setEmail("user@example.com");
        user.setFull_name("User");
        user.setPassword("password");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        assertEquals("This username already exists. Please choose another", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAuthenticate_Success() {
        String rawPassword = "secretPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        User authenticated = userService.authenticate("john_doe", rawPassword);
        assertNotNull(authenticated);
        assertEquals("john_doe", authenticated.getUsername());
    }

    @Test
    void testAuthenticate_WrongPassword_ThrowsException() {
        String encodedPassword = passwordEncoder.encode("correctPassword");

        User user = new User();
        user.setUsername("john_doe");
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.authenticate("john_doe", "wrongPassword"));
    }

    @Test
    void testAuthenticate_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.authenticate("nonexistent", "password"));
    }
}
