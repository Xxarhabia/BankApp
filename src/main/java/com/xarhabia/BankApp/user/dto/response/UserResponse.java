package com.xarhabia.BankApp.user.dto.response;

import java.time.OffsetDateTime;

public record UserResponse(
        String fullName,
        String document,
        String email,
        Boolean isActive,
        OffsetDateTime createAt
) {}
