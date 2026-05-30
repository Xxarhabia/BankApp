package com.xarhabia.BankApp.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "El nombre del usuario es obligatirio")
        @Size(max = 80)
        String fullName,

        @NotBlank(message = "El documento es obligatorio")
        @Size(max = 10)
        String document,

        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
