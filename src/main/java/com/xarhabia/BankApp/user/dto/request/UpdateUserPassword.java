package com.xarhabia.BankApp.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserPassword(
        @NotBlank
        String newPassword
) {}
