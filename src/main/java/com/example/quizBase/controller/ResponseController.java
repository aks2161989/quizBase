package com.example.quizBase.controller;

import com.example.quizBase.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.quizBase.service.ResponseService;

import java.util.List;

@RestController
@RequestMapping("/responses")
public class ResponseController {
    private final ResponseService responseService;

    @Autowired
    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getResponseById(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Response ID must be a positive number");
        }
        return responseService.findResponseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/result/id/{resultId}")
    public ResponseEntity<List<Response>> getResponsesByResult(@PathVariable Long resultId) {
        if (resultId <= 0) {
            throw new IllegalArgumentException("Result ID must be a positive number");
        }
        List<Response> responses = responseService.findResponsesByResult(resultId);
        return responses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(responses);
    }
    @GetMapping("/question/id/{questionId}")
    public ResponseEntity<List<Response>> getResponsesByQuestion(@PathVariable Long questionId) {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Question ID must be a positive number");
        }
        List<Response> responses = responseService.findResponsesByQuestion(questionId);
        return responses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(responses);
    }
}
