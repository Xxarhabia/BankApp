package com.xarhabia.BankApp.transaction.dto.response;

public record MovementsResponse(
        String movementId,
        String typeMovement,
        String amount,
        String accountNumber
) {}
