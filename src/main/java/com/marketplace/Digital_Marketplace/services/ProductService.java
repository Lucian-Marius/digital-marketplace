package com.marketplace.Digital_Marketplace.services;

import com.marketplace.Digital_Marketplace.models.Product;
import com.marketplace.Digital_Marketplace.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getApprovedProducts() {
        return productRepository.findByApprovedTrue();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
    
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
