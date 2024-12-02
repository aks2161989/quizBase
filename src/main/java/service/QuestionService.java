package service;

import dao.QuestionRepository;
import models.Question;
import models.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Optional<Question> findQuestionById(Long id) {
        return Optional.ofNullable(questionRepository.findById(id));
    }

    public List<Question> findQuestionsByQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz must not be null or empty");
        }
        return questionRepository.findByQuiz(quiz);
    }
    public List<Question> findQuestionsByQuiz(Long quizId) {
        if (quizId == null) {
            throw new IllegalArgumentException("Quiz ID must not be null");
        }
        return questionRepository.findByQuiz(quizId);
    }
    public List<Question> findAllQuestions() {
        List<Question> questions = questionRepository.findAllQuestions();
        return questions != null ? questions : Collections.emptyList();
    }
    public void createQuestion(Question question){
        questionRepository.save(question);
    }
}
