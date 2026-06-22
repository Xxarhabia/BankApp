package com.xarhabia.BankApp.account.service.impl;

import com.xarhabia.BankApp.account.dto.request.CreateAccountRequest;
import com.xarhabia.BankApp.account.dto.response.AccountResponse;
import com.xarhabia.BankApp.account.entity.AccountEntity;
import com.xarhabia.BankApp.account.repository.AccountRepository;
import com.xarhabia.BankApp.account.service.AccountService;
import com.xarhabia.BankApp.audit.Auditable;
import com.xarhabia.BankApp.exceptions.BusinessException;
import com.xarhabia.BankApp.user.entity.UserEntity;
import com.xarhabia.BankApp.user.repository.UserRepository;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import com.xarhabia.BankApp.utils.log.RequestResponseLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Auditable(action = "CREATE_ACCOUNT")
    public GeneralResponse createAccount(String document, CreateAccountRequest request) {

        StringBuilder sbLog = new StringBuilder();
        sbLog.append(RequestResponseLog.logRequestTransaction(request));
        UserEntity user;

        if(userRepository.findByDocument(document).isEmpty()) {
            throw new BusinessException("USUARIO_NO_ENCONTRADO",
                    "El usuario con el documento [" + document + "] no existe");
        }

        user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BusinessException("USUARIO_NO_ENCONTRADO",
                        "El usuario con el documento [" + document + "] no existe"));

        validateAccountCreation(document, request.typeAccount());

        AccountEntity account = AccountEntity.builder()
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .typeAccount(request.typeAccount().toUpperCase())
                .description(request.description())
                .user(user)
                .build();
        account = accountRepository.save(account);
        sbLog.append("\nSe ha creado la cuenta: ").append(account.getAccountNumber())
                .append("\nTipo de cuenta: ").append(account.getTypeAccount())
                .append("\nPara el usuario con documento: ").append(user.getDocument());

        return new GeneralResponse("00", "Cuenta creada correctamente", true, toResponse(account));
    }

    @Override
    @Transactional(readOnly = true)
    @Auditable(action = "FIND_USER_ACCOUNTS")
    public GeneralResponse getAllAccounts(String document) {
        UserEntity user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BusinessException("USUARIO_NO_ENCONTRADO",
                        "El usuario con el documento [" + document + "] no existe"));

        List<AccountResponse> accounts = accountRepository.findByUser(user)
                .stream().map(this::toResponse).toList();

        return new GeneralResponse("00", "Listado de cuentas de usuario", true, accounts);
    }

    private AccountResponse toResponse(AccountEntity a) {
        return new AccountResponse(
                a.getAccountNumber(),
                a.getBalance(),
                a.getTypeAccount(),
                a.getDescription(),
                a.getCreatedAt()
        );
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;

        do {
            accountNumber = String.format("%010d",
                    random.nextLong(1_000_000_0000L));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private void validateAccountCreation(String document, String typeAccount) {
        long count = accountRepository.countAccountsByDocumentAndType(document, typeAccount);

        System.out.println("documento: " + document);
        System.out.println("Tipo: " + typeAccount);
        System.out.println("count: " + count);

        switch (typeAccount.toUpperCase()) {
            case "AHORROS":
                if (count >= 2) {
                    throw new BusinessException(
                            "LIMITE_DE_CUENTAS",
                            "El usuario ya tiene el maximo de cuentas de ahorro");
                }
                break;

            case "CORRIENTE":
                if (count >= 1) {
                    throw new BusinessException(
                            "LIMITE_CUENTA_CORRIENTE",
                            "El usuario ya tiene una cuenta corriente");
                }
                break;

            case "NOMINA":
                if (count >= 1) {
                    throw new BusinessException(
                            "LIMITE_CUENTA_NOMINA",
                            "El usuario ya tiene una cuenta nomina");
                }
                break;

            default:
                throw new BusinessException(
                        "TIPO_CUENTA_INVALIDO",
                        "El tipo de cuenta no es valido");
        }
    }

}
