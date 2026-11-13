package com.example.quizBase.config;

import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public AuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        String token = (session != null) ? (String) session.getAttribute("token") : null;

        // Also support Authorization: Bearer <token> header for REST API clients
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null) {
            try {
                String username = jwtService.validateToken(token);
                request.setAttribute("authenticatedUsername", username);
                return true;
            } catch (Exception e) {
                // Token invalid or expired - proceed to redirection/401 below
            }
        }

        String requestUri = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        if ((requestUri != null && requestUri.contains("/api/")) || (acceptHeader != null && acceptHeader.contains("application/json"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized or expired token\"}");
            return false;
        }

        response.sendRedirect(request.getContextPath() + "/users/login");
        return false;
    }
}
