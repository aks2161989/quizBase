package com.example.quizBase.service;

import com.example.quizBase.dao.CategoryRepository;
import com.example.quizBase.models.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findCategoryById(Long id) {
        return Optional.ofNullable(categoryRepository.findById(id));
    }

    public Optional<Category> findCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        return categoryRepository.findByName(name);
    }

    public List<Category> findAllCategories() {
        List<Category> categories = categoryRepository.findAllCategories();
        return categories != null ? categories : Collections.emptyList();
    }
    @Transactional
    public void createCategory(Category category){

        if(category.getName() == null || category.getName().isEmpty()){
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        categoryRepository.save(category);
    }
    public void updateCategory(Category category){
        categoryRepository.update(category);
    }
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }
}
