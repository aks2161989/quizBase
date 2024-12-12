package com.example.quizBase.controller;

import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class DashboardController {

    private final JwtService jwtService;

    public DashboardController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("dashboard")
    public String dashboard(Model model,
                            HttpSession session){
        try{
            String token = (String)session.getAttribute("token");
            if(token == null){
                return "redirect:/users/login";
            }
            String username = jwtService.validateToken(token);
            model.addAttribute("username", username);
            return "dashboard";
            }
        catch(Exception e){
            return "redirect:/users/login";
        }
     }
}
