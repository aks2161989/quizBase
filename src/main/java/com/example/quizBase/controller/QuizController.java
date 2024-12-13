package com.example.quizBase.controller;

import com.example.quizBase.models.*;
import com.example.quizBase.service.CategoryService;
import com.example.quizBase.service.QuizService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import dto.QuizForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;
    private final UserService userService;
    private final JwtService jwtService;

    private final CategoryService categoryService;
    @Autowired
    public QuizController(QuizService quizService, UserService userService,
                          JwtService jwtService, CategoryService categoryService)
    {
        this.quizService = quizService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizService.findQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<Quiz>> getQuizzesByCategory(@PathVariable Long categoryId) {
        List<Quiz> quizzes = quizService.findQuizzesByCategory(categoryId);
        return quizzes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(quizzes);
    }
    @GetMapping("/users/{creatorId}")
    public ResponseEntity<List<Quiz>> getQuizzesByCreator(@PathVariable Long creatorId) {
        List<Quiz> quizzes = quizService.findQuizzesByCreator(creatorId);
        return quizzes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(quizzes);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<List<Quiz>> searchQuizzesByTitle(@PathVariable String title) {
        if (title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        List<Quiz> quizzes = quizService.searchQuizzesByTitle(title);
        return quizzes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(quizzes);
    }
    
    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.findAllQuizzes();
        return quizzes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(quizzes);
    }

    @GetMapping("/listallquizzes")
    public String listAllQuizzes(Model model, HttpSession session){
        String token = (String) session.getAttribute("token");
        if(token == null){
            return "redirect:/users/login";
        }

        try {
            String username = jwtService.validateToken(token);
            Map<Category, List<Quiz>> quizzesByCategory = quizService.groupQuizzesByCategory();
            model.addAttribute("quizzesByCategory", quizzesByCategory);
            return "listallquizzes";
        }catch(Exception e){
            return "redirect:/users/login";
        }
    }

    @GetMapping("/create")
    public String create(Model model, HttpSession session){
        String token = (String) session.getAttribute("token");
        if(token == null){
            return "redirect:/users/login";
        }
        try {
            String username = jwtService.validateToken(token);
            List<Category> categories = categoryService.findAllCategories();
            model.addAttribute("categories", categories);
            return "createquiz";
        }catch(Exception e){
            return "redirect:/users/login";
        }
    }

    @PostMapping("/createaquiz")
    public String createQuiz(@Valid @ModelAttribute QuizForm quizForm,
                              BindingResult result,
                              HttpSession session, Model model) {

        String token = (String) session.getAttribute("token");
        if(token == null){
            return "redirect:/users/login";
        }

        /*
        quiz.setCreator(creator);
        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                question.setQuiz(quiz);
                if (question.getOptions() != null) {
                    for (Option option : question.getOptions()) {
                        option.setQuestion(question);
                        option.setQuiz(quiz);
                    }
                }
            }
        } */

        if(result.hasErrors()){
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("error", "Please correct the errors in the form");
            return "createquiz";
        }
        try{
            String username = jwtService.validateToken(token);
            User creator = userService.findUserByUsername(username).get();
            System.out.println("Category Name from Form: " + quizForm.getCategoryName());

            quizService.createQuiz(quizForm.toQuiz(creator, categoryService.findCategoryById(Long.parseLong(quizForm.getCategoryName())).get()));
            return "redirect:/dashboard";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred. please try again");
            return "createquiz";
        }
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<String> updateQuiz(@PathVariable Long quizId, @Valid
    @RequestBody Quiz updatedQuiz, @RequestHeader("Authorization") String token) {
       String username;
       try{
           if(token.startsWith("Bearer")){
               token = token.substring(7);
           }
           username = jwtService.validateToken(token);
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
       }

       User user = userService.findUserByUsername(username).
               orElseThrow(() -> new IllegalArgumentException("User not found"));

        Quiz existingQuiz = quizService.findQuizById(quizId).
                orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        if(!existingQuiz.getCreator().getUsername().equals(user.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only edit your own quiz.");
        }

        updatedQuiz.setId(existingQuiz.getId());
        quizService.updateQuiz(updatedQuiz);

        return ResponseEntity.ok("Quiz updated successfully");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        // Find the preexisting quiz
        String username;
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            username = jwtService.validateToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body("Invalid or expired token");
        }

        try {
            quizService.deleteQuiz(id, username);
            return ResponseEntity.ok("Quiz deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }







    @PostMapping("/{quizId}/answers")
    public ResponseEntity<String> submitAnswers(@PathVariable Long quizId,
                                                @RequestBody List<Response> responses,
                                                @RequestHeader("Authorization")String token) {
        String username;
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            username = jwtService.validateToken(token);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid or expired token");
        }

        User user = userService.findUserByUsername(username)
                        .orElseThrow(() -> new IllegalArgumentException("user not found"));
        quizService.submitAnswers(quizId, user, responses);

        return ResponseEntity.ok("Answers submitted successfully");
    }


    }
