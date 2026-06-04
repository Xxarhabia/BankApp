package com.xarhabia.BankApp.transaction.service.impl;

import com.xarhabia.BankApp.account.entity.AccountEntity;
import com.xarhabia.BankApp.account.repository.AccountRepository;
import com.xarhabia.BankApp.exceptions.BusinessException;
import com.xarhabia.BankApp.transaction.dto.request.RegisterDepositWithdrawalRequest;
import com.xarhabia.BankApp.transaction.dto.request.RegisterTransferRequest;
import com.xarhabia.BankApp.transaction.dto.response.TransferResponse;
import com.xarhabia.BankApp.transaction.entity.MovementEntity;
import com.xarhabia.BankApp.transaction.entity.TransferEntity;
import com.xarhabia.BankApp.transaction.repository.MovementRepository;
import com.xarhabia.BankApp.transaction.repository.TransferRepository;
import com.xarhabia.BankApp.transaction.service.TransactionService;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import com.xarhabia.BankApp.utils.enums.MovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransferRepository transferRepository;
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;


    @Override
    public GeneralResponse registerTransfer(RegisterTransferRequest request) {
        AccountEntity source = accountRepository.findByAccountNumber(request.sourceAccountNumber())
                .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", "La cuenta proporcionada no existe"));
        AccountEntity destination = accountRepository.findByAccountNumber(request.destinationAccountNumber())
                .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", "La cuenta proporcionada no existe"));

        if (source.getBalance().compareTo(destination.getBalance()) < 0) {
            throw new BusinessException("SALDO_INSUFICIENTE",
                    "El saldo proporcinado no es sufuciente para realizar la transaccion");
        }

        source.setBalance(source.getBalance().subtract(request.amount()));
        destination.setBalance(destination.getBalance().add(request.amount()));

        TransferEntity transfer = transferRepository.save(
                TransferEntity.builder()
                        .amount(request.amount())
                        .build()
        );

        MovementEntity debitMovement = MovementEntity.builder()
                .account(source)
                .transfer(transfer)
                .amount(request.amount())
                .typeMovement(MovementType.TRANSFERIR_DEBITO)
                .build();

        MovementEntity creditMovement = MovementEntity.builder()
                .account(destination)
                .transfer(transfer)
                .amount(request.amount())
                .typeMovement(MovementType.TRANSFERIR_CREDITO)
                .build();

        movementRepository.save(debitMovement);
        movementRepository.save(creditMovement);

        accountRepository.save(source);
        accountRepository.save(destination);

        return new GeneralResponse(
                "00",
                "Transferencia realizada exitosamente",
                true,
                new TransferResponse(
                        request.sourceAccountNumber(),
                        request.destinationAccountNumber(),
                        request.amount(),
                        "TRANSFERENCIA"
                )
        );
    }

    @Override
    public GeneralResponse registerDeposit(String accountNumber, RegisterDepositWithdrawalRequest request) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", "La cuenta proporcionada no existe"));

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        MovementEntity depositMovement = MovementEntity.builder()
                .typeMovement(MovementType.DEPOSITO)
                .amount(request.amount())
                .account(account)
                .build();

        movementRepository.save(depositMovement);

        return new GeneralResponse("00", "Deposito realizado exitosamente", true, account);
    }

    @Override
    public GeneralResponse registerWithdrawal(String accountNumber, RegisterDepositWithdrawalRequest request) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", "La cuenta proporcionada no existe"));

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException(
                    "SALDO_INSUFICIENTE",
                    "El saldo actual no es suficiente para realizar la transaccion");
        }

        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);

        MovementEntity withdrawalMovement = MovementEntity.builder()
                .typeMovement(MovementType.RETIRO)
                .amount(request.amount())
                .account(account)
                .build();

        movementRepository.save(withdrawalMovement);

        return new GeneralResponse("00", "Retiro realizado exitosamente", true, account);
    }

    @Override
    public GeneralResponse getAllTransactions() {
        List<MovementEntity> movements = movementRepository.findAll();
        return new GeneralResponse("00", "Listado de transacciones", true, movements);
    }

    @Override
    public GeneralResponse getTransactionById(Long id) {
        MovementEntity movement = movementRepository.findById(id).orElseThrow(
                () -> new BusinessException("TRANSACCION_NO_ENCONTRADA", "La transaccion no existe"));
        return new GeneralResponse("00", "Transaccion", true, movement);
    }

    @Override
    public GeneralResponse checkUserTransactionsByAccount(String accountNumber) {
        List<MovementEntity> movements = movementRepository.findByAccountNumber(accountNumber);
        return new GeneralResponse("00", "Listado de transacciones de la cuenta", true, movements);
    }

}
