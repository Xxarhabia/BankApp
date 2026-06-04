package com.xarhabia.BankApp.transaction.repository;

import com.xarhabia.BankApp.transaction.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Integer> {
}
