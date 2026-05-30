package com.xarhabia.BankApp.account.dto.response;

import java.time.OffsetDateTime;

public record AccountResponse(
        String accountNumber,
        double balance,
        String accountType,
        String description,
        OffsetDateTime createAt
) {}
