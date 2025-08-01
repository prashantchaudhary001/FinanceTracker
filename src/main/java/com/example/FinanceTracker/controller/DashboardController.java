package com.example.FinanceTracker.controller;

import com.example.FinanceTracker.entity.Transaction;
import com.example.FinanceTracker.service.TransactionService;
import com.example.FinanceTracker.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

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

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        boolean isLoggedIn = username != null;
        
        if (isLoggedIn) {
            // Calculate totals with username parameter
            BigDecimal totalIncome = transactionService.getTotalIncome(username);
            BigDecimal totalExpenses = transactionService.getTotalExpenses(username);
            BigDecimal balance = totalIncome.subtract(totalExpenses);
            
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("balance", balance);
            
            // Get recent transactions with username parameter
            List<Transaction> recentTransactions = transactionService.getRecentTransactions(username);
            model.addAttribute("recentTransactions", recentTransactions);
        } else {
            model.addAttribute("totalIncome", BigDecimal.ZERO);
            model.addAttribute("totalExpenses", BigDecimal.ZERO);
            model.addAttribute("balance", BigDecimal.ZERO);
        }
        
        model.addAttribute("username", username);
        model.addAttribute("isLoggedIn", isLoggedIn);
        
        return "dashboard";
    }
}




