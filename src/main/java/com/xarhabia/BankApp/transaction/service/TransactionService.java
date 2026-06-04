package com.xarhabia.BankApp.transaction.service;

import com.xarhabia.BankApp.transaction.dto.request.RegisterDepositWithdrawalRequest;
import com.xarhabia.BankApp.transaction.dto.request.RegisterTransferRequest;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;

import java.util.List;

public interface TransactionService {

    GeneralResponse registerTransfer(RegisterTransferRequest request);
    GeneralResponse registerDeposit(String document, RegisterDepositWithdrawalRequest request);
    GeneralResponse registerWithdrawal(String document, RegisterDepositWithdrawalRequest request);
    GeneralResponse getAllTransactions();
    GeneralResponse getTransactionById(Long id);
    GeneralResponse checkUserTransactionsByAccount(String accountNumber);

}
