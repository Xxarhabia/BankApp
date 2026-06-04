package com.xarhabia.BankApp.transaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegisterTransferRequest(
        @NotBlank(message = "El numero de la cuenta de origen es obligatorio")
        @Size(max = 10)
        String sourceAccountNumber,

        @NotBlank(message = "El numero de la cuenta de destino es obligatorio")
        @Size(max = 10)
        String destinationAccountNumber,

        @NotNull(message = "El monto de la transaccion es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal amount
) {}
