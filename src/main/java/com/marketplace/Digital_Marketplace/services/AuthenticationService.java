package com.marketplace.Digital_Marketplace.services;

import com.marketplace.Digital_Marketplace.models.User;
import com.marketplace.Digital_Marketplace.models.Role;
import com.marketplace.Digital_Marketplace.repositories.UserRepository;
import com.marketplace.Digital_Marketplace.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Register a new user
     */
    public User registerUser(String firstName, String lastName, String username, 
                            String email, String password, String accountType) throws Exception {
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Email cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new Exception("Password must be at least 6 characters long");
        }
        
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByUsernameOrEmail(username, email);
        if (existingUser.isPresent()) {
            throw new Exception("Username or email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(firstName + " " + lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        
        // Assign role based on account type
        List<Role> roles = new ArrayList<>();
        String roleName = "ROLE_BUYER";
        if ("seller".equalsIgnoreCase(accountType)) {
            roleName = "ROLE_SELLER";
        }
        
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isPresent()) {
            roles.add(role.get());
        } else {
            // Create role if it doesn't exist
            Role newRole = new Role();
            newRole.setName(roleName);
            Role savedRole = roleRepository.save(newRole);
            roles.add(savedRole);
        }
        user.setRoles(roles);
        
        // Save user
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user login
     */
    public User authenticateLogin(String username, String password) throws Exception {
        
        // Find user by username or email
        Optional<User> user = userRepository.findByUsernameOrEmail(username, username);
        
        if (user.isEmpty()) {
            throw new Exception("User not found");
        }
        
        User foundUser = user.get();
        
        // Check if user is enabled
        if (!foundUser.getEnabled()) {
            throw new Exception("User account is disabled");
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, foundUser.getPassword())) {
            throw new Exception("Invalid password");
        }
        
        return foundUser;
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }
    
    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }
}
