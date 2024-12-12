package com.example.quizBase.controller;

import com.example.quizBase.util.JwtService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final JwtService jwtService;

    public HomeController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/welcome")
    public String welcomePage(Model model, @RequestParam(value = "token", required = false) String token){
        String username = jwtService.validateToken(token.replace("Bearer ", ""));
        model.addAttribute("username", username);
        System.out.println("The username is "+ username);
        return "welcome";
    }
}
