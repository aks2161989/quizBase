package com.example.quizBase.controller;

import com.example.quizBase.dto.*;
import com.example.quizBase.models.Category;
import com.example.quizBase.models.Option;
import com.example.quizBase.models.Question;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRestController {

    private final QuizService quizService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final JwtService jwtService;

    @Autowired
    public QuizRestController(QuizService quizService, UserService userService,
                              CategoryService categoryService, JwtService jwtService) {
        this.quizService = quizService;
        this.userService = userService;
        this.categoryService = categoryService;
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

    private QuizSummaryDto toSummaryDto(Quiz quiz) {
        if (quiz == null) return null;
        int qCount = 0;
        try {
            qCount = (quiz.getQuestions() != null) ? quiz.getQuestions().size() : 0;
        } catch (Exception ignored) {
        }

        Long catId = (quiz.getCategory() != null) ? quiz.getCategory().getId() : null;
        String catName = (quiz.getCategory() != null) ? quiz.getCategory().getName() : "Uncategorized";
        Long creatorId = (quiz.getCreator() != null) ? quiz.getCreator().getId() : null;
        String creatorName = (quiz.getCreator() != null) ? quiz.getCreator().getUsername() : "Unknown";

        return new QuizSummaryDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                catId,
                catName,
                creatorId,
                creatorName,
                qCount,
                quiz.getCreated_date()
        );
    }

    private QuizDetailDto toDetailDto(Quiz quiz, boolean hideAnswers) {
        if (quiz == null) return null;
        QuizDetailDto dto = new QuizDetailDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCreatedDate(quiz.getCreated_date());

        if (quiz.getCategory() != null) {
            dto.setCategoryId(quiz.getCategory().getId());
            dto.setCategoryName(quiz.getCategory().getName());
        }
        if (quiz.getCreator() != null) {
            dto.setCreatorId(quiz.getCreator().getId());
            dto.setCreatorUsername(quiz.getCreator().getUsername());
        }

        List<QuestionDto> questionDtos = new ArrayList<>();
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                QuestionDto qDto = new QuestionDto();
                qDto.setId(q.getId());
                qDto.setText(q.getText());

                List<OptionDto> optDtos = new ArrayList<>();
                if (q.getOptions() != null) {
                    for (Option opt : q.getOptions()) {
                        OptionDto optDto = new OptionDto();
                        optDto.setId(opt.getId());
                        optDto.setText(opt.getText());
                        optDto.setIsCorrect(hideAnswers ? null : opt.getIs_correct());
                        optDtos.add(optDto);
                    }
                }
                qDto.setOptions(optDtos);
                questionDtos.add(qDto);
            }
        }
        dto.setQuestions(questionDtos);
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<QuizSummaryDto>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.findAllQuizzes();
        if (quizzes == null || quizzes.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<QuizSummaryDto> dtos = quizzes.stream().map(this::toSummaryDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizService.findQuizByIdWithQuestionsAndOptions(id);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDetailDto(quiz, false));
    }

    @GetMapping("/{id}/play")
    public ResponseEntity<?> getQuizForPlay(@PathVariable Long id) {
        Quiz quiz = quizService.findQuizByIdWithQuestionsAndOptions(id);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDetailDto(quiz, true));
    }

    @GetMapping("/my-quizzes")
    public ResponseEntity<?> getMyQuizzes(HttpServletRequest request) {
        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.findUserByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        List<Quiz> quizzes = quizService.findQuizzesByCreator(user.getId());
        if (quizzes == null || quizzes.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<QuizSummaryDto> dtos = quizzes.stream().map(this::toSummaryDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody QuizCreateRequest requestBody, HttpServletRequest request) {
        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        User creator = userService.findUserByUsername(username).orElse(null);
        if (creator == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        if (requestBody.getTitle() == null || requestBody.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
        }

        if (requestBody.getCategoryId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category is required"));
        }

        Category category = categoryService.findCategoryById(requestBody.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Selected category not found"));
        }

        if (requestBody.getQuestions() == null || requestBody.getQuestions().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "At least one question is required"));
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(requestBody.getTitle().trim());
        quiz.setDescription(requestBody.getDescription());
        quiz.setCategory(category);
        quiz.setCreator(creator);

        List<Question> questions = new ArrayList<>();
        for (QuizCreateRequest.QuestionItem qItem : requestBody.getQuestions()) {
            if (qItem.getText() == null || qItem.getText().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Question text cannot be empty"));
            }

            Question question = new Question();
            question.setText(qItem.getText().trim());
            question.setQuiz(quiz);

            if (qItem.getOptions() == null || qItem.getOptions().size() < 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "Each question must have at least 2 options"));
            }

            boolean hasCorrect = false;
            List<Option> options = new ArrayList<>();
            for (QuizCreateRequest.OptionItem oItem : qItem.getOptions()) {
                Option option = new Option();
                option.setText(oItem.getText() != null ? oItem.getText().trim() : "");
                boolean isCorrect = oItem.getIsCorrect();
                option.setIs_correct(isCorrect);
                if (isCorrect) hasCorrect = true;
                option.setQuestion(question);
                option.setQuiz(quiz);
                options.add(option);
            }

            if (!hasCorrect) {
                return ResponseEntity.badRequest().body(Map.of("error", "Each question must have at least one correct option"));
            }

            question.setOptions(options);
            questions.add(question);
        }

        quiz.setQuestions(questions);
        quizService.createQuiz(quiz);

        return ResponseEntity.status(HttpStatus.CREATED).body(toSummaryDto(quiz));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @RequestBody QuizCreateRequest requestBody, HttpServletRequest request) {
        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Quiz existing = quizService.findQuizById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existing.getCreator().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You can only edit quizzes you created"));
        }

        Category category = categoryService.findCategoryById(requestBody.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Category not found"));
        }

        try {
            Quiz updated = new Quiz();
            updated.setTitle(requestBody.getTitle().trim());
            updated.setDescription(requestBody.getDescription());
            updated.setCategory(category);

            List<Question> updatedQuestions = new ArrayList<>();
            for (QuizCreateRequest.QuestionItem qItem : requestBody.getQuestions()) {
                Question question = new Question();
                question.setText(qItem.getText().trim());
                question.setQuiz(updated);

                List<Option> updatedOptions = new ArrayList<>();
                for (QuizCreateRequest.OptionItem oItem : qItem.getOptions()) {
                    Option option = new Option();
                    option.setText(oItem.getText().trim());
                    option.setIs_correct(oItem.getIsCorrect());
                    option.setQuestion(question);
                    option.setQuiz(updated);
                    updatedOptions.add(option);
                }
                question.setOptions(updatedOptions);
                updatedQuestions.add(question);
            }
            updated.setQuestions(updatedQuestions);

            quizService.editQuiz(id, username, updated);

            Quiz reloaded = quizService.findQuizByIdWithQuestionsAndOptions(id);
            return ResponseEntity.ok(toSummaryDto(reloaded));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Failed to update quiz"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id, HttpServletRequest request) {
        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        try {
            quizService.deleteQuiz(id, username);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long id, @RequestBody QuizSubmissionDto submission, HttpServletRequest request) {
        String username = extractUsername(request);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        User user = userService.findUserByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        Quiz quiz = quizService.findQuizByIdWithQuestionsAndOptions(id);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        Map<Long, Long> answers = submission.getAnswers() != null ? submission.getAnswers() : Collections.emptyMap();
        int score = quizService.evaluateQuiz(id, answers, user);

        int total = (quiz.getQuestions() != null) ? quiz.getQuestions().size() : 0;
        int unanswered = Math.max(0, total - answers.size());
        int incorrect = Math.max(0, total - score - unanswered);
        double percentage = total > 0 ? ((double) score / total) * 100.0 : 0.0;
        boolean passed = percentage >= 60.0;

        QuizResultDto resultDto = new QuizResultDto(
                quiz.getId(),
                quiz.getTitle(),
                score,
                total,
                score,
                incorrect,
                unanswered,
                Math.round(percentage * 10.0) / 10.0,
                passed
        );

        return ResponseEntity.ok(resultDto);
    }
}
