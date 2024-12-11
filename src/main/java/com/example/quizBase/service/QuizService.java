package com.example.quizBase.service;

import com.example.quizBase.dao.QuizRepository;
import com.example.quizBase.dao.ResponseRepository;
import com.example.quizBase.dao.ResultRepository;
import com.example.quizBase.models.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.quizBase.dao.OptionRepository;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository;
    private final ResponseRepository responseRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, OptionRepository optionRepository, ResponseRepository responseRepository, ResultRepository resultRepository) {
        this.quizRepository = quizRepository;
        this.optionRepository = optionRepository;
        this.responseRepository = responseRepository;
        this.resultRepository = resultRepository;
    }


    public Optional<Quiz> findQuizById(Long id) {
        return Optional.ofNullable(quizRepository.findById(id));
    }

    public List<Quiz> findQuizzesByCreator(User creator) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator must not be null or empty");
        }
        return quizRepository.findByCreator(creator);
    }
    public List<Quiz> findQuizzesByCreator(Long creatorId) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID must not be null");
        }
        return quizRepository.findByCreator(creatorId);
    }

    public List<Quiz> findQuizzesByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }
        return quizRepository.findByCategory(category);
    }
    public List<Quiz> findQuizzesByCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category Id must not be null");
        }
        return quizRepository.findByCategory(categoryId);
    }

    public List<Quiz> searchQuizzesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        return quizRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Quiz> findAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAllQuizzes();
        return quizzes != null ? quizzes : Collections.emptyList();
    }

    @Transactional
    public void createQuiz(Quiz quiz){
        quizRepository.save(quiz);
    }

    @Transactional
    public void updateQuiz(Quiz quiz){
        Quiz existingQuiz = quizRepository.findById(quiz.getId());

        if(existingQuiz == null){
                throw  new IllegalArgumentException("Quiz with id " + quiz.getId() + " not found");
        }
        existingQuiz.setTitle(quiz.getTitle());
        existingQuiz.setCategory(quiz.getCategory());
        existingQuiz.setDescription(quiz.getDescription());

        quizRepository.update(existingQuiz);

    }
    public void deleteQuiz(Long id){
        quizRepository.deleteById(id);
    }

    @Transactional
    public void submitAnswers(Long quizId, User user, List<Response> responses) {
        Quiz quiz = null;
        Option selectedOption = null;
        try {
            quiz = quizRepository.findById(quizId);
        }catch(Exception e) {
            if (quiz == null) {
                throw new IllegalArgumentException("Quiz not found");
            }
        }

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setAttempted_date(Date.from(LocalDateTime.now().toLocalDate().
                atStartOfDay(ZoneId.systemDefault()).toInstant()));
         result.setScore(0);
        resultRepository.save(result);
         int score = 0;

         for(Response r: responses){
             try {
                 selectedOption = optionRepository.findById(r.getSelectedOption().getId());
             }
             catch (Exception e){
                 throw new IllegalArgumentException("Option not found");
             }

             Question question = selectedOption.getQuestion();
             if (question == null) {
                 throw new IllegalArgumentException("Question not found for the selected option");
             }

             r.setQuestion(question);
             r.setResult(result);

             if(selectedOption.getIs_correct()){
                score++;
             }
             r.setResult(result);
             responseRepository.save(r);
         }
         result.setScore(score);
    }

    }

