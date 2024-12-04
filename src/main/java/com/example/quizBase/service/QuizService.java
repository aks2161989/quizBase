package com.example.quizBase.service;

import com.example.quizBase.dao.QuizRepository;
import com.example.quizBase.models.Category;
import com.example.quizBase.models.Quiz;
import com.example.quizBase.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
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
    public void createQuiz(Quiz quiz){
        quizRepository.save(quiz);
    }
}
