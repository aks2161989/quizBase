package com.example.quizBase.controller;

import com.example.quizBase.dto.CategoryDto;
import com.example.quizBase.models.Category;
import com.example.quizBase.models.User;
import com.example.quizBase.service.CategoryService;
import com.example.quizBase.service.UserService;
import com.example.quizBase.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
public class CategoryRestControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CategoryRestController categoryRestController;

    @Test
    void testGetAllCategories() {
        User creator = new User();
        creator.setUsername("john_doe");
        Category cat1 = new Category("Science", creator);
        cat1.setId(1L);

        when(categoryService.findAllCategories()).thenReturn(List.of(cat1));

        ResponseEntity<List<CategoryDto>> response = categoryRestController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Science", response.getBody().get(0).getName());
        assertEquals("john_doe", response.getBody().get(0).getCreatorUsername());
    }

    @Test
    void testCreateCategory_Success() {
        User creator = new User();
        creator.setUsername("john_doe");

        when(request.getAttribute("authenticatedUsername")).thenReturn("john_doe");
        when(userService.findUserByUsername("john_doe")).thenReturn(Optional.of(creator));

        ResponseEntity<?> response = categoryRestController.createCategory(Map.of("name", "History"), request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof CategoryDto);
        CategoryDto dto = (CategoryDto) response.getBody();
        assertEquals("History", dto.getName());
    }
}
