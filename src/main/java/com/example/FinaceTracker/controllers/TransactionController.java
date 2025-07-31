package com.example.FinaceTracker.controllers;

import com.example.FinaceTracker.entity.Transaction;
import com.example.FinaceTracker.entity.TransactionType;
import com.example.FinaceTracker.service.TransactionService;
import com.example.FinaceTracker.service.CategoryService;
import com.example.FinaceTracker.config.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import java.beans.PropertyEditorSupport;
import com.example.FinaceTracker.entity.Category;

import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtUtil jwtUtil;

    private String getUsernameFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token != null && jwtUtil.validateToken(token)) {
                    return jwtUtil.getUsernameFromToken(token);
                }
            }
        }
        return null;
    }

    @GetMapping
    public String listTransactions(Model model, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        if (username == null) return "redirect:/login";

        model.addAttribute("transactions", transactionService.getAllTransactions(username));
        model.addAttribute("totalIncome", transactionService.getTotalIncome(username));
        model.addAttribute("totalExpenses", transactionService.getTotalExpenses(username));
        model.addAttribute("username", username);
        return "transactions/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        if (username == null) return "redirect:/login";

        populateFormModel(model);
        model.addAttribute("transaction", new Transaction());
        return "transactions/add";
    }

    @PostMapping("/add")
    public String addTransaction(@Valid @ModelAttribute Transaction transaction,
                                 BindingResult result,
                                 Model model,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {

        String username = getUsernameFromRequest(request);
        if (username == null) return "redirect:/login";

        if (result.hasErrors()) {
            populateFormModel(model);
            return "transactions/add";
        }

        try {
            transactionService.saveTransaction(transaction, username);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction added successfully!");
            return "redirect:/transactions";
        } catch (Exception e) {
            populateFormModel(model);
            model.addAttribute("error", "Error adding transaction: " + e.getMessage());
            return "transactions/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String username = getUsernameFromRequest(request);
        if (username == null) return "redirect:/login";

        Optional<Transaction> transactionOpt = transactionService.getTransactionById(id);
        if (transactionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Transaction not found.");
            return "redirect:/transactions";
        }

        model.addAttribute("transaction", transactionOpt.get());
        populateFormModel(model);
        return "transactions/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @Valid @ModelAttribute Transaction transaction,
                                    BindingResult result,
                                    Model model,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {
        String username = getUsernameFromRequest(request);
        if (username == null) return "redirect:/login";

        if (result.hasErrors()) {
            populateFormModel(model);
            return "transactions/edit";
        }

        try {
            transaction.setId(id);
            transactionService.saveTransaction(transaction, username);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
            return "redirect:/transactions";
        } catch (Exception e) {
            populateFormModel(model);
            model.addAttribute("error", "Error updating transaction: " + e.getMessage());
            return "transactions/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        if (username == null) return "redirect:/login";

        try {
            transactionService.deleteTransaction(id);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting transaction: " + e.getMessage());
        }

        return "redirect:/transactions";
    }

    @ModelAttribute
    public void preprocessTransaction(@ModelAttribute Transaction transaction) {
        // Handle empty category selection
        if (transaction.getCategory() != null && 
            (transaction.getCategory().getId() == null || transaction.getCategory().getId() <= 0)) {
            transaction.setCategory(null);
        }
    }

    private void populateFormModel(Model model) {
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("categories", categoryService.getAllCategories());
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Category.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty() || "0".equals(text)) {
                    setValue(null);
                } else {
                    try {
                        Long id = Long.parseLong(text);
                        Category category = categoryService.findById(id);
                        setValue(category);
                    } catch (NumberFormatException e) {
                        setValue(null);
                    }
                }
            }
        });
    }
}
