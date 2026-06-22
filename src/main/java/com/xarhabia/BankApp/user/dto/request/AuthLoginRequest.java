package com.xarhabia.BankApp.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank
        String document,

        @NotBlank
        String password
) {}
