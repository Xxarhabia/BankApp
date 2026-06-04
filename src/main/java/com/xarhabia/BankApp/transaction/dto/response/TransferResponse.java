package com.xarhabia.BankApp.transaction.dto.response;

import java.math.BigDecimal;

public record TransferResponse(
    String sourceAccountNumber,
    String destinationAccountNumber,
    BigDecimal amount,
    String typeMovement
) {}
