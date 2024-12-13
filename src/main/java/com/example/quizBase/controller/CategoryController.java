package com.example.quizBase.controller;

import com.example.quizBase.models.Category;
import com.example.quizBase.models.User;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.quizBase.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final JwtService jwtService;

    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, JwtService jwtService, UserService userService) {
        this.categoryService = categoryService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.findCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name must not be null or empty");
        }
        return categoryService.findCategoryByName(name.trim().toLowerCase())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        return categories.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(categories);
    }

    @GetMapping("/create")
    public String showCreateCategoryPage(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if(token == null){
            return "redirect:/users/login";
        }
        try{
            String username = jwtService.validateToken(token);
            model.addAttribute("category", new Category());
            model.addAttribute("username", username);
            return "createcategory";
        }catch(Exception e){
            return "redirect:/users/login";
        }
    }
        @PostMapping("/create/submit")
    public String createCategory(@ModelAttribute Category category, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if(token == null){
            return "redirect:/users/login";
        }
        try {
            String username = jwtService.validateToken(token);
            User user = userService.findUserByUsername(username).get();
            category.setCreator(user);
            categoryService.createCategory(category);
            return "redirect:/dashboard";
        }catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "createcategory";
        } catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "redirect:/users/login";
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable @Min(1)  Long id, @Valid @RequestBody Category updatedCategory) {
        if (!id.equals(updatedCategory.getId())) {
            throw new IllegalArgumentException("Mismatch between path variable ID and request body ID");
        }
        categoryService.updateCategory(updatedCategory);
        return ResponseEntity.ok("Category updated successfully");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Min(1) Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    }
