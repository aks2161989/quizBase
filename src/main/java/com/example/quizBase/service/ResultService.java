package com.example.quizBase.service;

import com.example.quizBase.dao.ResultRepository;
import com.example.quizBase.models.Quiz;
import com.example.quizBase.models.Result;
import com.example.quizBase.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultService {
    private final ResultRepository resultRepository;

    @Autowired
    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public Optional<Result> findResultById(Long id) {
        return Optional.ofNullable(resultRepository.findById(id));
    }

    public List<Result> findResultsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        return resultRepository.findByUser(user);
    }
    public List<Result> findResultsByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return resultRepository.findByUser(userId);
    }
    public List<Result> findResultsByQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz must not be null");
        }
        return resultRepository.findByQuiz(quiz);
    }
    public List<Result> findResultsByQuiz(Long quizId) {
        if (quizId == null) {
            throw new IllegalArgumentException("Quiz ID must not be null");
        }
        return resultRepository.findByQuiz(quizId);
    }
    public List<Result> findTop10ResultsByQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz must not be null");
        }
        return resultRepository.findTop10ByQuizOrderByScoreDesc(quiz);
    }
    public List<Result> findTop10ResultsByQuiz(Long quizId) {
        if (quizId == null) {
            throw new IllegalArgumentException("Quiz ID must not be null");
        }
        return resultRepository.findTop10ByQuizOrderByScoreDesc(quizId);
    }

    @Transactional
    public void createResult(Result result){
        resultRepository.save(result);
    }
}
