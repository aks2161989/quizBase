package service;

import dao.ResultRepository;
import models.*;
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
    public List<Result> findResultsByQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz must not be null");
        }
        return resultRepository.findByQuiz(quiz);
    }
    public List<Result> findTop10ResultsByQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz must not be null");
        }
        return resultRepository.findTop10ByQuizOrderByScoreDesc(quiz);
    }
}
