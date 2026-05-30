package com.xarhabia.BankApp.account.service;

import com.xarhabia.BankApp.account.dto.request.CreateAccountRequest;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;

public interface AccountService {

    GeneralResponse createAccount(String document, CreateAccountRequest request);
    GeneralResponse getAllAccounts(String document);
}
