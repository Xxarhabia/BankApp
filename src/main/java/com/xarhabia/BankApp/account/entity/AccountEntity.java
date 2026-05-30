package com.xarhabia.BankApp.account.entity;

import com.xarhabia.BankApp.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
@Builder
public class AccountEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_seq"
    )
    @SequenceGenerator(
            name = "account_seq",
            sequenceName = "account_seq",
            allocationSize = 1
    )
    private Long accountId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private double balance;

    @Column(name = "type_account", nullable = false)
    private String typeAccount;

    private String description;

    @Column(unique = true)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "document")
    private UserEntity user;
}
