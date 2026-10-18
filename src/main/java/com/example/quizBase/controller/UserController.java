package com.example.quizBase.controller;

import com.example.quizBase.models.User;
import com.example.quizBase.util.JwtService;
import dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.quizBase.service.UserService;
import io.jsonwebtoken.Jwts;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;


    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        return userService.findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        return ResponseEntity.ok(userService.existsByUsername(username));
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }


    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/signin")
    public String signIn(@RequestParam String username,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {
        try{
            User authenticatedUser = userService.authenticate(username, password);
            String token = jwtService.generateToken(authenticatedUser.getUsername());

            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("token", token);

            return "redirect:/dashboard";
        }catch (IllegalArgumentException e){
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                                               @RequestParam String email,
                                               @RequestParam String password,
                                               @RequestParam String fullname,
                                               Model model) {
        try {
         User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFull_name(fullname);
        user.setEmail(email);


            userService.registerUser(user);
            model.addAttribute("message", "Your account has been created");
            return "redirect:/users/login";
        }catch (IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "redirect:users/register";
        }
    }

}
