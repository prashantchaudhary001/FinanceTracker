package com.example.FinanceTracker.service;

import com.example.FinanceTracker.entity.Transaction;
import com.example.FinanceTracker.entity.TransactionType;
import com.example.FinanceTracker.entity.User;
import com.example.FinanceTracker.entity.Category;
import com.example.FinanceTracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    public List<Transaction> getAllTransactionsByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return transactionRepository.findByUserIdOrderByDateDesc(user.getId());
        }
        return List.of();
    }

    public List<Transaction> getAllTransactions(String username) {
        return getAllTransactionsByUsername(username);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public void saveTransaction(Transaction transaction, String username) {
        System.out.println("=== SAVE TRANSACTION SERVICE DEBUG ===");
        System.out.println("Input transaction type: " + transaction.getType());
        System.out.println("Input transaction amount: " + transaction.getAmount());
        System.out.println("Username: " + username);

        if (username != null) {
            User user = userService.findByUsername(username);
            if (user != null) {
                transaction.setUser(user);
                System.out.println("User set: " + user.getUsername() + " (ID: " + user.getId() + ")");
            } else {
                System.out.println("ERROR: User not found for username: " + username);
                throw new RuntimeException("User not found: " + username);
            }
        } else {
            System.out.println("ERROR: Username is null");
            throw new RuntimeException("Username cannot be null");
        }

        // Fix the category issue - fetch the managed category from database
        if (transaction.getCategory() != null && transaction.getCategory().getId() != null
                && transaction.getCategory().getId() > 0) {
            Category managedCategory = categoryService.findById(transaction.getCategory().getId());
            if (managedCategory != null) {
                transaction.setCategory(managedCategory);
                System.out.println(
                        "Category set: " + managedCategory.getName() + " (ID: " + managedCategory.getId() + ")");
            } else {
                System.out.println("WARNING: Category not found, setting to null");
                transaction.setCategory(null);
            }
        } else {
            System.out.println("No category selected, setting to null (will display as 'Uncategorized')");
            transaction.setCategory(null);
        }

        System.out.println("About to save to repository...");
        try {
            Transaction savedTransaction = transactionRepository.save(transaction);
            System.out.println("SUCCESS: Transaction saved with ID: " + savedTransaction.getId());
            System.out.println("Saved transaction type: " + savedTransaction.getType());
            System.out.println("Saved transaction amount: " + savedTransaction.getAmount());
        } catch (Exception e) {
            System.out.println("ERROR saving transaction: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public BigDecimal getTotalIncome(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return transactionRepository.sumByTypeAndUserId(TransactionType.INCOME, user.getId());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpenses(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return transactionRepository.sumByTypeAndUserId(TransactionType.EXPENSE, user.getId());
        }
        return BigDecimal.ZERO;
    }

    public List<Transaction> getRecentTransactions(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return transactionRepository.findRecentTransactionsByUserId(user.getId(), PageRequest.of(0, 5));
        }
        return List.of();
    }

    public Map<String, Double> getMonthlyIncome(String username) {
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        User user = userService.findByUsername(username);
        
        if (user == null) return monthlyData;

        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            // Get all transactions for this user and filter by date and type
            List<Transaction> allTransactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
            
            double total = allTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .filter(t -> !t.getDate().isBefore(monthStart) && !t.getDate().isAfter(monthEnd))
                    .mapToDouble(t -> t.getAmount().doubleValue())
                    .sum();

            String monthName = monthStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyData.put(monthName, total);
        }
        return monthlyData;
    }

    public Map<String, Double> getMonthlyExpenses(String username) {
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        User user = userService.findByUsername(username);
        
        if (user == null) return monthlyData;

        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            // Get all transactions for this user and filter by date and type
            List<Transaction> allTransactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
            
            double total = allTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .filter(t -> !t.getDate().isBefore(monthStart) && !t.getDate().isAfter(monthEnd))
                    .mapToDouble(t -> t.getAmount().doubleValue())
                    .sum();

            String monthName = monthStart.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyData.put(monthName, total);
        }
        return monthlyData;
    }
}
