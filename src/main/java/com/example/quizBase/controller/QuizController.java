package com.example.quizBase.controller;

import com.example.quizBase.models.Category;
import com.example.quizBase.models.Quiz;
import com.example.quizBase.service.QuizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
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
        quizService.createQuiz(quiz);
        return ResponseEntity.ok("Quiz added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateQuiz(@PathVariable @Min(1)  Long id, @Valid @RequestBody Quiz updatedQuiz) {
        updatedQuiz.setId(id);
        quizService.updateQuiz(updatedQuiz);
        return ResponseEntity.ok("Quiz updated successfully");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable @Min(1) Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

}
