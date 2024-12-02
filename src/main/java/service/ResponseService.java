package service;

import dao.ResponseRepository;
import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponseService {

    private final ResponseRepository responseRepository;

    @Autowired
    public ResponseService(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public Optional<Response> findResponseById(Long id) {
        return Optional.ofNullable(responseRepository.findById(id));
    }

    public List<Response> findResponsesByResult(Result result) {
        if (result == null) {
            throw new IllegalArgumentException("Result must not be null");
        }
        return responseRepository.findByResult(result);
    }
    public List<Response> findResponsesByResult(Long resultId) {
        if (resultId == null) {
            throw new IllegalArgumentException("Result ID must not be null");
        }
        return responseRepository.findByResult(resultId);
    }
    public List<Response> findResponsesByQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question must not be null");
        }
        return responseRepository.findByQuestion(question);
    }
    public List<Response> findResponsesByQuestion(Long questionId) {
        if (questionId == null) {
            throw new IllegalArgumentException("Question ID must not be null");
        }
        return responseRepository.findByQuestion(questionId);
    }
}
