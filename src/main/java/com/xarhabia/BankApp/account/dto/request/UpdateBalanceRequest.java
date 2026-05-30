package com.xarhabia.BankApp.account.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateBalanceRequest(
        @NotNull(message = "El monto debe ser obligatorio")
        double amount,
        String type
) {}
