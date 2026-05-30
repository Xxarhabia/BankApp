package com.xarhabia.BankApp.user.controller;

import com.xarhabia.BankApp.user.dto.request.CreateUserRequest;
import com.xarhabia.BankApp.user.dto.request.UpdateUserPassword;
import com.xarhabia.BankApp.user.service.UserService;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/v1/users
    @PostMapping
    public ResponseEntity<GeneralResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createNewUser(request));
    }

    // GET /api/v1/users
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    // GET /api/v1/users/{document}
    @GetMapping("/{document}")
    public ResponseEntity<GeneralResponse> getUserById(@PathVariable String document) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(document));
    }

    // PATCH /api/v1/users/{document}/password
    @PatchMapping("/{document}/password")
    public ResponseEntity<GeneralResponse> updatePassword(
            @PathVariable String document,
            @Valid @RequestBody UpdateUserPassword request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserPassword(document, request));
    }
}
