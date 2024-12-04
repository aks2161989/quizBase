package com.example.quizBase.service;

import com.example.quizBase.dao.OptionRepository;
import com.example.quizBase.models.Option;
import com.example.quizBase.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OptionService {
    private final OptionRepository optionRepository;

    @Autowired
    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public Optional<Option> findOptionById(Long id) {
        return Optional.ofNullable(optionRepository.findById(id));
    }

    public List<Option> findOptionsByQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question must not be null or empty");
        }
        return optionRepository.findByQuestion(question);
    }
    public List<Option> findOptionsByQuestion(Long questionId) {
        if (questionId == null) {
            throw new IllegalArgumentException("Question ID must not be null");
        }
        return optionRepository.findByQuestion(questionId);
    }
    public void createOption(Option option){
        optionRepository.save(option);
    }
}
