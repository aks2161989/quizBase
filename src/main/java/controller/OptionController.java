package controller;

import models.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.OptionService;

import java.util.List;

@RestController
@RequestMapping("/options")
public class OptionController {
    private final OptionService optionService;

    @Autowired
    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Option> getOptionById(@PathVariable Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Option ID must be a positive number");
        }
        return optionService.findOptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Option>> getOptionsByQuestion(@PathVariable Long questionId) {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Question ID must be a positive number");
        }
        List<Option> options = optionService.findOptionsByQuestion(questionId);
        return options.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(options);
    }
}
