package com.xarhabia.BankApp.user.service;

import com.xarhabia.BankApp.user.dto.request.CreateUserRequest;
import com.xarhabia.BankApp.user.dto.request.UpdateUserPassword;
import com.xarhabia.BankApp.user.entity.UserEntity;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;

import java.util.List;

public interface UserService {

    GeneralResponse createNewUser(CreateUserRequest request);

    // ADMIN ONLY
    GeneralResponse getAllUsers();

    // ADMIN ONLY
    GeneralResponse getUserById(String document);

    GeneralResponse updateUserPassword(String document, UpdateUserPassword request);
}
