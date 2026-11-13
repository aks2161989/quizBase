package com.example.quizBase.controller;

import com.example.quizBase.dto.CategoryDto;
import com.example.quizBase.models.Category;
import com.example.quizBase.models.User;
import com.example.quizBase.service.CategoryService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public CategoryRestController(CategoryService categoryService, UserService userService, JwtService jwtService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    private CategoryDto toDto(Category cat) {
        if (cat == null) return null;
        String creatorName = null;
        try {
            if (cat.getCreator() != null) {
                creatorName = cat.getCreator().getUsername();
            }
        } catch (Exception ignored) {
        }
        return new CategoryDto(cat.getId(), cat.getName(), creatorName);
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

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<Category> list = categoryService.findAllCategories();
        if (list == null || list.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<CategoryDto> dtos = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return categoryService.findCategoryById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String name = body.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category name is required"));
        }

        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        User creator = userService.findUserByUsername(username).orElse(null);
        if (creator == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        try {
            Category category = new Category(name.trim(), creator);
            categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Bad request"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category name is required"));
        }

        Category existing = categoryService.findCategoryById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(name.trim());
        categoryService.updateCategory(existing);
        return ResponseEntity.ok(toDto(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
