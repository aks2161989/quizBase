package com.example.quizBase.controller;

import com.example.quizBase.dto.AuthResponse;
import com.example.quizBase.dto.RegisterRequest;
import com.example.quizBase.models.Role;
import com.example.quizBase.models.User;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthRestController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Username and password are required");
            return ResponseEntity.badRequest().body(err);
        }

        try {
            User authenticatedUser = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            String token = jwtService.generateToken(authenticatedUser.getUsername());

            AuthResponse response = new AuthResponse(
                    token,
                    authenticatedUser.getUsername(),
                    authenticatedUser.getRole() != null ? authenticatedUser.getRole().name() : Role.ROLE_STUDENT.name(),
                    authenticatedUser.getEmail(),
                    authenticatedUser.getFull_name()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Authentication error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Username, email and password are required");
            return ResponseEntity.badRequest().body(err);
        }

        try {
            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail().trim());
            user.setFull_name(request.getFullname() != null ? request.getFullname().trim() : request.getUsername().trim());

            if (request.getRole() != null) {
                try {
                    user.setRole(Role.valueOf(request.getRole()));
                } catch (Exception e) {
                    user.setRole(Role.ROLE_STUDENT);
                }
            } else {
                user.setRole(Role.ROLE_STUDENT);
            }

            userService.registerUser(user);

            String token = jwtService.generateToken(user.getUsername());
            AuthResponse response = new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getRole().name(),
                    user.getEmail(),
                    user.getFull_name()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String username = (String) request.getAttribute("authenticatedUsername");
        if (username == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    username = jwtService.validateToken(authHeader.substring(7));
                } catch (Exception ignored) {
                }
            }
        }

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
        }

        return userService.findUserByUsername(username)
                .map(user -> ResponseEntity.ok(new AuthResponse(
                        null,
                        user.getUsername(),
                        user.getRole() != null ? user.getRole().name() : Role.ROLE_STUDENT.name(),
                        user.getEmail(),
                        user.getFull_name()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
