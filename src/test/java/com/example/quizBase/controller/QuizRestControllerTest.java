package com.example.quizBase.controller;

import com.example.quizBase.dto.QuizCreateRequest;
import com.example.quizBase.dto.QuizDetailDto;
import com.example.quizBase.dto.QuizResultDto;
import com.example.quizBase.dto.QuizSubmissionDto;
import com.example.quizBase.dto.QuizSummaryDto;
import com.example.quizBase.models.*;
import com.example.quizBase.service.CategoryService;
import com.example.quizBase.service.QuizService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizRestControllerTest {

    @Mock
    private QuizService quizService;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private QuizRestController quizRestController;

    private Quiz testQuiz;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("instructor1");

        testCategory = new Category("Science", testUser);
        testCategory.setId(1L);

        testQuiz = new Quiz();
        testQuiz.setId(10L);
        testQuiz.setTitle("Physics 101");
        testQuiz.setDescription("Introductory physics quiz");
        testQuiz.setCategory(testCategory);
        testQuiz.setCreator(testUser);

        Question q1 = new Question();
        q1.setId(100L);
        q1.setText("What is the speed of light?");
        q1.setQuiz(testQuiz);

        Option o1 = new Option(q1, "3x10^8 m/s", true, testQuiz);
        o1.setId(1001L);
        Option o2 = new Option(q1, "3x10^6 m/s", false, testQuiz);
        o2.setId(1002L);
        q1.setOptions(List.of(o1, o2));

        testQuiz.setQuestions(List.of(q1));
    }

    @Test
    void testGetAllQuizzes() {
        when(quizService.findAllQuizzes()).thenReturn(List.of(testQuiz));

        ResponseEntity<List<QuizSummaryDto>> response = quizRestController.getAllQuizzes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        QuizSummaryDto dto = response.getBody().get(0);
        assertEquals("Physics 101", dto.getTitle());
        assertEquals("Science", dto.getCategoryName());
        assertEquals("instructor1", dto.getCreatorUsername());
    }

    @Test
    void testGetQuizForPlay_HidesIsCorrect() {
        when(quizService.findQuizByIdWithQuestionsAndOptions(10L)).thenReturn(testQuiz);

        ResponseEntity<?> response = quizRestController.getQuizForPlay(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof QuizDetailDto);
        QuizDetailDto detail = (QuizDetailDto) response.getBody();
        assertEquals("Physics 101", detail.getTitle());
        assertEquals(1, detail.getQuestions().size());
        assertNull(detail.getQuestions().get(0).getOptions().get(0).getIsCorrect(),
                "isCorrect must be stripped to prevent cheating via API inspection");
    }

    @Test
    void testSubmitQuiz_Success() {
        when(request.getAttribute("authenticatedUsername")).thenReturn("student1");
        User student = new User();
        student.setUsername("student1");
        when(userService.findUserByUsername("student1")).thenReturn(Optional.of(student));
        when(quizService.findQuizByIdWithQuestionsAndOptions(10L)).thenReturn(testQuiz);
        when(quizService.evaluateQuiz(eq(10L), anyMap(), eq(student))).thenReturn(1);

        QuizSubmissionDto submission = new QuizSubmissionDto(Map.of(100L, 1001L));
        ResponseEntity<?> response = quizRestController.submitQuiz(10L, submission, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof QuizResultDto);
        QuizResultDto result = (QuizResultDto) response.getBody();
        assertEquals(1, result.getScore());
        assertEquals(1, result.getTotal());
        assertEquals(100.0, result.getPercentage());
        assertTrue(result.isPassed());
    }

    @Test
    void testUpdateQuiz_Success() {
        when(request.getAttribute("authenticatedUsername")).thenReturn("instructor1");
        when(quizService.findQuizById(10L)).thenReturn(Optional.of(testQuiz));
        when(categoryService.findCategoryById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(quizService).editQuiz(eq(10L), eq("instructor1"), any(Quiz.class));
        when(quizService.findQuizByIdWithQuestionsAndOptions(10L)).thenReturn(testQuiz);

        QuizCreateRequest updateReq = new QuizCreateRequest();
        updateReq.setTitle("Updated Physics 101");
        updateReq.setDescription("Updated description");
        updateReq.setCategoryId(1L);

        QuizCreateRequest.QuestionItem qItem = new QuizCreateRequest.QuestionItem();
        qItem.setText("Updated question?");
        QuizCreateRequest.OptionItem oItem1 = new QuizCreateRequest.OptionItem();
        oItem1.setText("Option 1");
        oItem1.setIsCorrect(true);
        QuizCreateRequest.OptionItem oItem2 = new QuizCreateRequest.OptionItem();
        oItem2.setText("Option 2");
        oItem2.setIsCorrect(false);
        qItem.setOptions(List.of(oItem1, oItem2));
        updateReq.setQuestions(List.of(qItem));

        ResponseEntity<?> response = quizRestController.updateQuiz(10L, updateReq, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof QuizSummaryDto);
    }
}
