package com.xarhabia.BankApp.transaction.controller;

import com.xarhabia.BankApp.transaction.dto.request.RegisterDepositWithdrawalRequest;
import com.xarhabia.BankApp.transaction.dto.request.RegisterTransferRequest;
import com.xarhabia.BankApp.transaction.service.TransactionService;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /api/v1/transactions/transfer
    @PostMapping("/transfer")
    public ResponseEntity<GeneralResponse> transfer(@Valid @RequestBody RegisterTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.registerTransfer(request));
    }

    // PATCH /api/v1/transactions/#/deposit
    @PatchMapping("/{accountNumber}/deposit")
    public ResponseEntity<GeneralResponse> deposit(@PathVariable String accountNumber,
                                                   @Valid @RequestBody RegisterDepositWithdrawalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.registerDeposit(accountNumber, request));
    }

    // PATCH /api/v1/transactions/#/withdrawal
    @PatchMapping("/{accountNumber}/withdrawal")
    public ResponseEntity<GeneralResponse> withdraw(@PathVariable String accountNumber,
                                                    @Valid @RequestBody RegisterDepositWithdrawalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.registerWithdrawal(accountNumber, request));
    }

    // GET /api/v1/transactions
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllTransactions() {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransactions());
    }

    // GET /api/v1/transactions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionById(id));
    }

    // GET /api/v1/transactions/{accountNumber}
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<GeneralResponse> checkUserTransactionsByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.checkUserTransactionsByAccount(accountNumber));
    }
}
