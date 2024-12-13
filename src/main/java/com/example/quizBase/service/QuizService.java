package com.example.quizBase.service;

import com.example.quizBase.dao.QuizRepository;
import com.example.quizBase.dao.ResponseRepository;
import com.example.quizBase.dao.ResultRepository;
import com.example.quizBase.models.*;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.quizBase.dao.OptionRepository;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository;
    private final ResponseRepository responseRepository;
    private final ResultRepository resultRepository;
    private final SessionFactory sessionFactory;

    @Autowired
    public QuizService(QuizRepository quizRepository, OptionRepository optionRepository,
                       ResponseRepository responseRepository, ResultRepository resultRepository,
                       SessionFactory sessionFactory) {
        this.quizRepository = quizRepository;
        this.optionRepository = optionRepository;
        this.responseRepository = responseRepository;
        this.resultRepository = resultRepository;
        this.sessionFactory = sessionFactory;
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
    public Map<Category, List<Quiz>> groupQuizzesByCategory(){
        List<Quiz> quizzes = findAllQuizzes();
        Map<Category, List<Quiz>> quizMap = quizzes.stream().collect(Collectors.groupingBy(quiz -> quiz.getCategory()));
        return quizMap;
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

        Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        sessionFactory.getCurrentSession().merge(existingQuiz);

        Hibernate.initialize(existingQuiz.getQuestions());

        existingQuiz.setTitle(quiz.getTitle());
       // existingQuiz.setQuestions(quiz.getQuestions());
        existingQuiz.setCategory(quiz.getCategory());
        existingQuiz.setDescription(quiz.getDescription());


        // Remove questions & options from existing quiz that are not in quiz submitted in form, i.e. user wants to delete
        for(Question question: quiz.getQuestions()) {

            Optional<Question> questionInDb = existingQuiz.getQuestions()
                    .stream()
                    .filter(q -> q.getId().equals(question.getId()))
                    .findAny();
            if (questionInDb.isPresent()) {  // If a question is present
                questionInDb.get().setText(question.getText());
                questionInDb.get().setQuiz(question.getQuiz());

                for (Option o : question.getOptions()) {
                    Optional<Option> optionInDb = questionInDb.get().getOptions().
                            stream().filter(option -> option.getId().equals(o.getId())).findAny();
                    if (optionInDb.isPresent()) { //Option has not been deleted, so just update
                        optionInDb.get().setIs_correct(o.getIs_correct());
                        optionInDb.get().setText(o.getText());
                    } else { // new option has been added by user
                        Option optionNew = new Option(o.getQuestion(), o.getText(), o.getIs_correct(), o.getQuiz());
                        questionInDb.get().getOptions().add(optionNew);
                    }


                }
                List<Option> optionsToDelete = questionInDb.get().getOptions()
                        .stream()
                        .filter(o1 -> question.getOptions().stream().noneMatch(o2 -> o2.getId().equals(o1.getId())))
                        .collect(Collectors.toList()); // identify options not in updated quiz
                for (Option o : optionsToDelete) {
                    questionInDb.get().getOptions().remove(o); // delete the option
                }
            } else { // If the current question does not exist in DB, add it
                existingQuiz.getQuestions().add(question);
            }
        }

        List<Question> questionsToDelete = existingQuiz.getQuestions()
                .stream()
                .filter(q1 -> quiz.getQuestions().stream().noneMatch(q2 -> q2.getId().equals(q1.getId())))
                .collect(Collectors.toList());
        // identify questions not in updated quiz
        for (Question q : questionsToDelete) {
            for(Option o: q.getOptions()){
                q.getOptions().remove(o);
            }
            existingQuiz.getQuestions().remove(q); // delete the question
        }
        quizRepository.update(existingQuiz);
transaction.commit();
    }

    @Transactional
    public void deleteQuiz(Long id, String username){
        Quiz quiz =  quizRepository.findById(id);

        if(quiz == null) {
            throw new IllegalArgumentException("No quiz found with the id " + id);
        }

        if(!quiz.getCreator().getUsername().equals(username)){
            throw new IllegalArgumentException("You are not allowed to delete this quiz");
        }

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

