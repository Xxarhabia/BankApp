package com.xarhabia.BankApp.transaction.entity;


import com.xarhabia.BankApp.account.entity.AccountEntity;
import com.xarhabia.BankApp.utils.enums.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movements")
@Builder
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_movement", nullable = false)
    private MovementType typeMovement; // deposito, transferencia, retiro

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private TransferEntity transfer;

}
