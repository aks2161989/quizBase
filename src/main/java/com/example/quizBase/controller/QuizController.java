package com.example.quizBase.controller;

import com.example.quizBase.models.*;
import com.example.quizBase.service.QuizService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public QuizController(QuizService quizService, UserService userService, JwtService jwtService)
    {
        this.quizService = quizService;
        this.userService = userService;
        this.jwtService = jwtService;
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
    @PostMapping
    public ResponseEntity<String> createQuiz(@Valid @RequestBody Quiz quiz) {
        User creator = userService.findUserByUsername(quiz.getCreator().getUsername()).
                orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        }
        quizService.createQuiz(quiz);
        return ResponseEntity.ok("Quiz added successfully");
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
