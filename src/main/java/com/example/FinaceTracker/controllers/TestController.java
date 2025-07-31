package com.example.FinaceTracker.controllers;

import com.example.FinaceTracker.entity.User;
import com.example.FinaceTracker.service.UserService;
import com.example.FinaceTracker.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class TestController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/test-login")
    public String testLogin(HttpServletResponse response) {
        // Create a test user if it doesn't exist
        User testUser = userService.findByUsername("testuser");
        if (testUser == null) {
            testUser = new User("testuser", "test@example.com", "password");
            userService.saveUser(testUser);
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken("testuser");
        
        // Set JWT cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(jwtCookie);
        
        return "redirect:/";
    }
}