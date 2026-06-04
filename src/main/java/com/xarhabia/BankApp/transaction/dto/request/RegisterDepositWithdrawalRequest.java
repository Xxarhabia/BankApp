package com.xarhabia.BankApp.transaction.dto.request;

import java.math.BigDecimal;

public record RegisterDepositWithdrawalRequest(
        BigDecimal amount
) {}
