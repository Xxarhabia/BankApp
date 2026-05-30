package com.xarhabia.BankApp.account.repository;

import com.xarhabia.BankApp.account.entity.AccountEntity;
import com.xarhabia.BankApp.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    List<AccountEntity> findByUser(UserEntity user);
    boolean existsByAccountNumber(String accountNumber);
    @Query("""
        SELECT COUNT(a)
        FROM AccountEntity a
        WHERE a.user.document = :document
          AND UPPER(a.typeAccount) = UPPER(:typeAccount)
    """)
    long countAccountsByDocumentAndType(
            @Param("document") String document,
            @Param("typeAccount") String typeAccount);
}
