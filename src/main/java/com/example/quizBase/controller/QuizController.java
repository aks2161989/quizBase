package com.example.quizBase.controller;

import com.example.quizBase.dao.QuestionRepository;
import com.example.quizBase.dao.QuizRepository;
import com.example.quizBase.models.*;
import com.example.quizBase.service.CategoryService;
import com.example.quizBase.service.QuizService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import dto.QuizForm;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/quizzes")
public class QuizController {
    private static final Pattern ANSWER_PARAM = Pattern.compile("answers\\[(\\d+)]");

    private final QuizService quizService;
    private final UserService userService;
    private final JwtService jwtService;

    private final QuestionRepository questionRepository;

    private final CategoryService categoryService;

    private final QuizRepository quizRepository;

    private final SessionFactory sessionFactory;
    @Autowired
    public QuizController(QuizService quizService, UserService userService,
                          JwtService jwtService, CategoryService categoryService,
                          QuestionRepository questionRepository, QuizRepository quizRepository,
                          SessionFactory sessionFactory)
    {
        this.quizService = quizService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.categoryService = categoryService;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.sessionFactory = sessionFactory;
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
    @GetMapping("/edit/{quizId}")
    public String editQuizPage(@PathVariable Long quizId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/users/login";
        }
        try {
            String username = jwtService.validateToken(token);
            Quiz quiz = quizService.findQuizByIdWithQuestionsAndOptions(quizId);
            if (quiz == null || !quiz.getCreator().getUsername().equals(username)) {
                return "redirect:/quizzes/your-quizzes";
            }
            model.addAttribute("quiz", quiz);
            model.addAttribute("categories", categoryService.findAllCategories());
            return "editquiz";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/users/login";
        }
    }

    @PostMapping("/edit/{quizId}")
    public String editQuiz(@PathVariable Long quizId, @ModelAttribute QuizForm quizForm,
                           HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/users/login";
        }
        try {
            String username = jwtService.validateToken(token);
            User creator = userService.findUserByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
            Category category = categoryService.findCategoryById(Long.parseLong(quizForm.getCategoryName()))
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            quizService.editQuiz(quizId, username, quizForm.toQuiz(creator, category));
            return "redirect:/quizzes/your-quizzes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred while editing the quiz.");
            return "error";
        }
    }

    @PostMapping("/delete")
    public String deleteQuiz(@RequestParam Long quizId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/users/login";
        }

        try {
            String username = jwtService.validateToken(token);
            quizService.deleteQuiz(quizId, username);
            model.addAttribute("successMessage", "Quiz deleted successfully.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred while deleting the quiz.");
        }

        return "redirect:/quizzes/your-quizzes";
    }








    @GetMapping("/answer/{quizId}")
    public String answerQuiz(@PathVariable Long quizId, Model model, HttpSession session) {

        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/users/login";
        }

        try {

            String username = jwtService.validateToken(token);


            Quiz quiz = quizService.findQuizByIdWithQuestionsAndOptions(quizId);

            if (quiz == null) {
                model.addAttribute("error", "Quiz not found");
                return "redirect:/dashboard";
            }


            model.addAttribute("quiz", quiz);
            model.addAttribute("username", username);

            return "answerquiz";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/users/login";
        }
    }


    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Long quizId,
                             @RequestParam Map<String, String> params, // includes "answers[<questionId>]" -> optionId
                             HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/users/login";
        }

        try {
            Map<Long, Long> answers = new HashMap<>(); // Question ID -> Option ID
            for (Map.Entry<String, String> param : params.entrySet()) {
                Matcher m = ANSWER_PARAM.matcher(param.getKey());
                if (m.matches()) {
                    answers.put(Long.valueOf(m.group(1)), Long.valueOf(param.getValue()));
                }
            }

            String username = jwtService.validateToken(token);
            User user = userService.findUserByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

            int score = quizService.evaluateQuiz(quizId, answers, user);

            int total = quizService.findQuizByIdWithQuestionsAndOptions(quizId).getQuestions().size();
            model.addAttribute("score", score);
            model.addAttribute("total", total);
            model.addAttribute("unanswered", total - answers.size());
            return "result";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred.");
            return "error";
        }
    }
    @GetMapping("/your-quizzes")
    public String getUserQuizzes(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/users/login";
        }

        try {
            String username = jwtService.validateToken(token);
            User user = userService.findUserByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            List<Quiz> userQuizzes = quizService.findQuizzesByCreator(user.getId());
            model.addAttribute("userQuizzes", userQuizzes);
            model.addAttribute("username", username);
            return "dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/users/login";
        }
    }





}
