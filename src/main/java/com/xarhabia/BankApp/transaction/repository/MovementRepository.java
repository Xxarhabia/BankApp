package com.xarhabia.BankApp.transaction.repository;

import com.xarhabia.BankApp.transaction.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, Long> {

    @Query("""
        SELECT m 
        FROM MovementEntity m
        WHERE m.account.accountNumber = :accountNumber
     """)
    List<MovementEntity> findByAccountNumber(@Param("accountNumber") String accountNumber);
}
