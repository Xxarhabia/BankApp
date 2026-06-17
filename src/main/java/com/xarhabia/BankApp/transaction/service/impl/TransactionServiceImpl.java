package com.xarhabia.BankApp.transaction.service.impl;

import com.xarhabia.BankApp.account.entity.AccountEntity;
import com.xarhabia.BankApp.account.repository.AccountRepository;
import com.xarhabia.BankApp.exceptions.BusinessException;
import com.xarhabia.BankApp.transaction.dto.request.RegisterDepositWithdrawalRequest;
import com.xarhabia.BankApp.transaction.dto.request.RegisterTransferRequest;
import com.xarhabia.BankApp.transaction.dto.response.DepositWithdrawalResponse;
import com.xarhabia.BankApp.transaction.dto.response.MovementsResponse;
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

import static com.xarhabia.BankApp.utils.log.RequestResponseLog.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransferRepository transferRepository;
    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;


    @Override
    public GeneralResponse registerTransfer(RegisterTransferRequest request) {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append(logRequestTransaction(request));

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
        sbLog.append("\nSe ha realizado el debito en la cuenta: ").append(source.getAccountNumber());
        sbLog.append("\nDatos del debito: ").append(debitMovement.getMovementId()).append(", ")
                .append(debitMovement.getTypeMovement()).append(", ")
                .append(debitMovement.getAmount()).append(", ")
                .append(source.getAccountNumber()).append(", ")
                .append(source.getBalance()).append(", ")
                .append(source.getTypeAccount());

        movementRepository.save(creditMovement);
        sbLog.append("\nSe ha realizado el credito en la cuenta: ").append(destination.getAccountNumber());
        sbLog.append("\nDatos del credito: ").append(creditMovement.getMovementId()).append(", ")
                .append(creditMovement.getTypeMovement()).append(", ")
                .append(creditMovement.getAmount()).append(", ")
                .append(destination.getAccountNumber()).append(", ")
                .append(destination.getBalance()).append(", ")
                .append(destination.getTypeAccount());

        accountRepository.save(source);
        accountRepository.save(destination);

        TransferResponse response = new TransferResponse(
                request.sourceAccountNumber(),
                request.destinationAccountNumber(),
                request.amount(),
                "TRANSFERENCIA"
        );

        sbLog.append(logResponseTransaction(response));

        writeLog(sbLog.toString());

        return new GeneralResponse(
                "00",
                "Transferencia realizada exitosamente",
                true,
                response
        );
    }

    @Override
    public GeneralResponse registerDeposit(String accountNumber, RegisterDepositWithdrawalRequest request) {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append(logRequestTransaction(request));

        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", "La cuenta proporcionada no existe"));

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);
        sbLog.append("\nDatos actualizados en la cuenta: ").append(account.getAccountNumber());

        MovementEntity depositMovement = MovementEntity.builder()
                .typeMovement(MovementType.DEPOSITO)
                .amount(request.amount())
                .account(account)
                .build();

        movementRepository.save(depositMovement);
        DepositWithdrawalResponse response = new DepositWithdrawalResponse(
                depositMovement.getTypeMovement().toString(),
                depositMovement.getAmount().toString(),
                account.getAccountNumber(),
                account.getBalance().toString(),
                account.getTypeAccount()
        );

        sbLog.append("\nDatos del movimiento actualizados: ").append(response);
        sbLog.append(logResponseTransaction(response));

        writeLog(sbLog.toString());
        return new GeneralResponse("00", "Deposito realizado exitosamente", true, response);
    }

    @Override
    public GeneralResponse registerWithdrawal(String accountNumber, RegisterDepositWithdrawalRequest request) {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append(logRequestTransaction(request));

        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", "La cuenta proporcionada no existe"));

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException(
                    "SALDO_INSUFICIENTE",
                    "El saldo actual no es suficiente para realizar la transaccion");
        }

        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);
        sbLog.append("\nDatos actualizados en la cuenta: ").append(account.getAccountNumber());

        MovementEntity withdrawalMovement = MovementEntity.builder()
                .typeMovement(MovementType.RETIRO)
                .amount(request.amount())
                .account(account)
                .build();

        movementRepository.save(withdrawalMovement);
        DepositWithdrawalResponse response = new DepositWithdrawalResponse(
                withdrawalMovement.getTypeMovement().toString(),
                withdrawalMovement.getAmount().toString(),
                account.getAccountNumber(),
                account.getBalance().toString(),
                account.getTypeAccount()
        );

        sbLog.append("\nDatos del movimiento actualizados: ").append(response);
        sbLog.append(logResponseTransaction(response));

        writeLog(sbLog.toString());
        return new GeneralResponse("00", "Retiro realizado exitosamente", true, response);
    }

    @Override
    public GeneralResponse getAllTransactions() {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append(logRequestTransaction("Listado de transacciones"));

        List<MovementEntity> movements = movementRepository.findAll();
        List<MovementsResponse> response = movements.stream()
                        .map(mov -> new MovementsResponse(
                                mov.getMovementId().toString(),
                                mov.getTypeMovement().toString(),
                                mov.getAmount().toPlainString(),
                                mov.getAccount().getAccountNumber()
                        )).toList();

        for (MovementsResponse mov : response) {
            sbLog.append("\nID:").append(mov.movementId())
                    .append(", Tipo: ").append(mov.typeMovement())
                    .append(", Monto: ").append(mov.amount())
                    .append(", Numero Cuenta: ").append(mov.accountNumber());
        }
        sbLog.append(logResponseTransaction("Listado de transacciones"));

        writeLog(sbLog.toString());
        return new GeneralResponse("00", "Listado de transacciones", true, response);
    }

    @Override
    public GeneralResponse getTransactionById(Long id) {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append(logRequestTransaction("ID Transaction: " + id));

        MovementEntity movement = movementRepository.findById(id).orElseThrow(
                () -> new BusinessException("TRANSACCION_NO_ENCONTRADA", "La transaccion no existe"));
        sbLog.append("\nObtenido la transaccion con el id: ").append(id);

        MovementsResponse response = new MovementsResponse(
                movement.getMovementId().toString(),
                movement.getTypeMovement().toString(),
                movement.getAmount().toPlainString(),
                movement.getAccount().getAccountNumber()
        );

        sbLog.append(logResponseTransaction(response));
        writeLog(sbLog.toString());
        return new GeneralResponse("00", "Transaccion", true, response);
    }

    @Override
    public GeneralResponse checkUserTransactionsByAccount(String accountNumber) {
        StringBuilder sbLog = new StringBuilder();
        sbLog.append(logRequestTransaction(accountNumber));

        List<MovementEntity> movements = movementRepository.findByAccountNumber(accountNumber);
        List<MovementsResponse> response = movements.stream()
                .map(mov -> new MovementsResponse(
                        mov.getMovementId().toString(),
                        mov.getTypeMovement().toString(),
                        mov.getAmount().toPlainString(),
                        mov.getAccount().getAccountNumber()
                )).toList();

        for (MovementsResponse mov : response) {
            sbLog.append("\nID:").append(mov.movementId())
                    .append(", Tipo: ").append(mov.typeMovement())
                    .append(", Monto: ").append(mov.amount())
                    .append(", Numero Cuenta: ").append(mov.accountNumber());
        }
        sbLog.append(logResponseTransaction("Listado de transacciones de la cuenta: " + accountNumber));
        writeLog(sbLog.toString());
        return new GeneralResponse("00", "Listado de transacciones de la cuenta", true, response);
    }

}
