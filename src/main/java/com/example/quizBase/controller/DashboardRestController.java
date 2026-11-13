package com.example.quizBase.controller;

import com.example.quizBase.dto.DashboardStatsDto;
import com.example.quizBase.models.Category;
import com.example.quizBase.models.Quiz;
import com.example.quizBase.models.User;
import com.example.quizBase.service.CategoryService;
import com.example.quizBase.service.QuizService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final QuizService quizService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public DashboardRestController(QuizService quizService, CategoryService categoryService,
                                   UserService userService, JwtService jwtService) {
        this.quizService = quizService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    private String extractUsername(HttpServletRequest request) {
        String username = (String) request.getAttribute("authenticatedUsername");
        if (username != null) return username;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                return jwtService.validateToken(authHeader.substring(7));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.findUserByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        List<Quiz> allQuizzes = quizService.findAllQuizzes();
        int totalQuizzes = (allQuizzes != null) ? allQuizzes.size() : 0;

        List<Quiz> myQuizzes = quizService.findQuizzesByCreator(user.getId());
        int myQuizzesCount = (myQuizzes != null) ? myQuizzes.size() : 0;

        List<Category> categories = categoryService.findAllCategories();
        int totalCategories = (categories != null) ? categories.size() : 0;

        DashboardStatsDto stats = new DashboardStatsDto(
                totalQuizzes,
                myQuizzesCount,
                totalCategories,
                user.getUsername(),
                user.getRole() != null ? user.getRole().name() : "ROLE_STUDENT",
                user.getFull_name()
        );

        return ResponseEntity.ok(stats);
    }
}
