package com.marketplace.Digital_Marketplace.controllers;

import com.marketplace.Digital_Marketplace.models.User;
import com.marketplace.Digital_Marketplace.services.ProductService;
import com.marketplace.Digital_Marketplace.services.CategoryService;
import com.marketplace.Digital_Marketplace.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("approvedProducts", productService.getApprovedProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "index";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String remember,
            HttpSession session,
            Model model) {
        try {
            // Authenticate user
            User user = authenticationService.authenticateLogin(username, password);
            
            // Store user in session
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("fullName", user.getFullName());
            
            // Redirect to home
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }
    
    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String accountType,
            Model model) {
        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                throw new Exception("Passwords do not match");
            }
            
            // Validate password length
            if (password.length() < 6) {
                throw new Exception("Password must be at least 6 characters long");
            }
            
            // Register user
            User newUser = authenticationService.registerUser(firstName, lastName, username, 
                                                              email, password, accountType);
            
            // Redirect to login with success message
            model.addAttribute("success", "Registration successful! Please login.");
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("accountType", accountType);
            return "auth/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Clear all session attributes
        session.removeAttribute("userId");
        session.removeAttribute("username");
        session.removeAttribute("email");
        session.removeAttribute("fullName");
        
        // Invalidate the entire session
        session.invalidate();
        
        return "redirect:/";
    }
}