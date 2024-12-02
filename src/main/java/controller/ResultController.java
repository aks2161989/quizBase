package controller;

import models.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.ResultService;

import java.util.List;

@RestController
@RequestMapping("/results")
public class ResultController {
    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result> getResultById(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Result ID must be a positive number");
        }
        return resultService.findResultById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/id/{userId}")
    public ResponseEntity<List<Result>> getResultsByUser(@PathVariable Long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        List<Result> results = resultService.findResultsByUser(userId);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }
    @GetMapping("/quiz/id/{quizId}")
    public ResponseEntity<List<Result>> getResultsByQuiz(@PathVariable Long quizId) {
        if (quizId <= 0) {
            throw new IllegalArgumentException("Quiz ID must be a positive number");
        }
        List<Result> results = resultService.findResultsByQuiz(quizId);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }
    @GetMapping("/quiz/id/{quizId}/top10")
    public ResponseEntity<List<Result>> getTop10ResultsByQuiz(@PathVariable Long quizId) {
        if (quizId <= 0) {
            throw new IllegalArgumentException("Quiz ID must be a positive number");
        }
        List<Result> results = resultService.findTop10ResultsByQuiz(quizId);
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }
}
