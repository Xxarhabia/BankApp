package com.xarhabia.BankApp.account.controller;

import com.xarhabia.BankApp.account.dto.request.CreateAccountRequest;
import com.xarhabia.BankApp.account.service.AccountService;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{document}")
    public ResponseEntity<GeneralResponse> createUserAccount(
            @PathVariable String document,
            @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(document, request));
    }

    @GetMapping("/{document}")
    public ResponseEntity<GeneralResponse> getUserAccounts(@PathVariable String document) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllAccounts(document));
    }
}
