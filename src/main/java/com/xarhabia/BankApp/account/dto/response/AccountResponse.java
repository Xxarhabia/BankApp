package com.xarhabia.BankApp.account.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AccountResponse(
        String accountNumber,
        BigDecimal balance,
        String accountType,
        String description,
        OffsetDateTime createAt
) {}
