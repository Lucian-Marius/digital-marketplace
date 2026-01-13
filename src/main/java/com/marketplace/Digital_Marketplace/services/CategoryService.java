package com.marketplace.Digital_Marketplace.services;

import com.marketplace.Digital_Marketplace.models.Category;
import com.marketplace.Digital_Marketplace.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
