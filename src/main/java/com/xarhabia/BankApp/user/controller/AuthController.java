package com.xarhabia.BankApp.user.controller;

import com.xarhabia.BankApp.user.dto.request.AuthLoginRequest;
import com.xarhabia.BankApp.user.dto.request.CreateUserRequest;
import com.xarhabia.BankApp.user.service.impl.UserServiceImpl;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.loginUser(request));
    }

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createNewUser(request));
    }
}
