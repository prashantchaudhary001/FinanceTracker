package com.example.FinaceTracker.repository;

import com.example.FinaceTracker.entity.Transaction;
import com.example.FinaceTracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type AND t.user.id = :userId")
    BigDecimal sumByTypeAndUserId(@Param("type") TransactionType type, @Param("userId") Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findRecentTransactionsByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.username = :username AND t.type = :type AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUsernameAndTransactionTypeAndDateBetween(
        @Param("username") String username, 
        @Param("type") TransactionType type, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
}
