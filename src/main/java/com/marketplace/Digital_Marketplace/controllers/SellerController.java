package com.marketplace.Digital_Marketplace.controllers;

import com.marketplace.Digital_Marketplace.models.Product;
import com.marketplace.Digital_Marketplace.models.ProductFile;
import com.marketplace.Digital_Marketplace.models.User;
import com.marketplace.Digital_Marketplace.services.ProductService;
import com.marketplace.Digital_Marketplace.services.CategoryService;
import com.marketplace.Digital_Marketplace.services.StorageService;
import com.marketplace.Digital_Marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Seller Dashboard - View all products
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        Long userId = user.get().getId();
        List<Product> sellerProducts = productService.getProductsBySeller(userId);
        model.addAttribute("products", sellerProducts);
        model.addAttribute("username", username);

        return "seller/dashboard";
    }

    /**
     * Product Upload Form
     */
    @GetMapping("/products/create")
    public String createProductForm(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("username", authentication.getName());

        return "seller/upload";
    }

    /**
     * Handle Product Upload
     */
    @PostMapping("/products/create")
    public String uploadProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "price", defaultValue = "0") BigDecimal price,
            @RequestParam(value = "name", defaultValue = "Product") String name,
            @RequestParam(value = "description", defaultValue = "Digital product") String description,
            @RequestParam(value = "previewImage", required = false) MultipartFile previewImage,
            Authentication authentication,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> sellerOptional = userRepository.findByUsername(authentication.getName());
        if (sellerOptional.isEmpty()) {
            model.addAttribute("error", "Seller not found");
            return "seller/upload";
        }

        try {
            User seller = sellerOptional.get();

            // Upload product file
            String fileUrl = storageService.uploadFile(file, "products");

            // Upload preview image if provided
            String previewImageUrl = null;
            if (previewImage != null && !previewImage.isEmpty()) {
                previewImageUrl = storageService.uploadFile(previewImage, "previews");
            } else {
                // Use placeholder image
                previewImageUrl = "https://via.placeholder.com/300x300?text=Product+Image";
            }

            // Create product
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setSeller(seller);
            product.setApproved(false); // Admin approval required
            product.setCreatedAt(LocalDateTime.now());
            product.setPreviewImageUrl(previewImageUrl);

            product.setCategory(categoryService.getCategoryById(categoryId).orElse(null));

            // Create product file record
            ProductFile productFile = new ProductFile();
            productFile.setProduct(product);
            productFile.setFileUrl(fileUrl);
            productFile.setFileName(file.getOriginalFilename());
            productFile.setFileSize(file.getSize());
            productFile.setCreatedAt(LocalDateTime.now());

            product.getFiles().add(productFile);

            // Save product
            productService.createProduct(product);

            model.addAttribute("success", "Product uploaded successfully! Awaiting admin approval.");
            model.addAttribute("categories", categoryService.getAllCategories());

            return "seller/upload";

        } catch (IOException e) {
            model.addAttribute("error", "File upload failed: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "seller/upload";
        }
    }

    /**
     * Edit Product
     */
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        Long userId = user.get().getId();
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty() || !product.get().getSeller().getId().equals(userId)) {
            return "redirect:/seller/dashboard";
        }

        model.addAttribute("product", product.get());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("username", authentication.getName());

        return "seller/edit";
    }

    /**
     * Delete Product
     */
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        Long userId = user.get().getId();
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent() && product.get().getSeller().getId().equals(userId)) {
            productService.deleteProduct(id);
        }

        return "redirect:/seller/dashboard";
    }
}
