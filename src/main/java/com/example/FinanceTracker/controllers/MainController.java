package com.example.FinanceTracker.controllers;

import com.example.FinanceTracker.config.JwtUtil;
import com.example.FinanceTracker.service.TransactionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;

@Controller
public class MainController {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private String getUsernameFromRequest(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        if (token != null && !token.isEmpty() && jwtUtil.validateToken(token)) {
                            return jwtUtil.getUsernameFromToken(token);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error validating JWT: " + e.getMessage());
        }
        return null;
    }

    @GetMapping("/")
    public String dashboard(Model model, HttpServletRequest request) {
        try {
            String username = getUsernameFromRequest(request);
            boolean isLoggedIn = username != null;
            
            // Set default values
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpenses = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;
            
            // Only fetch transaction data if user is logged in
            if (isLoggedIn) {
                try {
                    totalIncome = transactionService.getTotalIncome(username);
                    totalExpenses = transactionService.getTotalExpenses(username);
                    balance = totalIncome.subtract(totalExpenses);
                    model.addAttribute("recentTransactions", transactionService.getRecentTransactions(username));
                    
                    // Add monthly data for charts
                    model.addAttribute("monthlyIncome", transactionService.getMonthlyIncome(username));
                    model.addAttribute("monthlyExpenses", transactionService.getMonthlyExpenses(username));
                    
                } catch (Exception e) {
                    System.out.println("Error fetching transaction data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("balance", balance);
            model.addAttribute("username", username);
            model.addAttribute("isLoggedIn", isLoggedIn);

            return "dashboard";
        } catch (Exception e) {
            System.out.println("Dashboard error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }
}



