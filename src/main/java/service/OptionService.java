package service;

import dao.OptionRepository;
import models.Option;
import models.Question;
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
}
