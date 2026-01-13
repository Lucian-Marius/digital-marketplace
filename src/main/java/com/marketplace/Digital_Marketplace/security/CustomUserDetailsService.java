package com.marketplace.Digital_Marketplace.security;

import com.marketplace.Digital_Marketplace.models.User;
import com.marketplace.Digital_Marketplace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, username);
        
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        User foundUser = user.get();
        
        // Convert roles to GrantedAuthority
        Collection<GrantedAuthority> authorities = foundUser.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(
            foundUser.getUsername(),
            foundUser.getPassword(),
            foundUser.getEnabled(),
            true,
            true,
            true,
            authorities
        );
    }
}
