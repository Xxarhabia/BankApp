package com.xarhabia.BankApp.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank(message = "El documento es obligatorior")
        @Size(max = 30)
        String typeAccount,

        String description
) {}
