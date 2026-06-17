package com.xarhabia.BankApp.transaction.dto.response;

public record DepositWithdrawalResponse(
        String movementType,
        String amount,
        String accountNumber,
        String balance,
        String typeAccount
) {}
