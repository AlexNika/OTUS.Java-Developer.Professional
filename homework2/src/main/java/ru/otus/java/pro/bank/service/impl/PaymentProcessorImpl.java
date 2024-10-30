package ru.otus.java.pro.bank.service.impl;

import ru.otus.java.pro.bank.entity.Account;
import ru.otus.java.pro.bank.entity.Agreement;
import ru.otus.java.pro.bank.service.AccountService;
import ru.otus.java.pro.bank.service.PaymentProcessor;
import ru.otus.java.pro.bank.service.exception.AccountException;

import java.math.BigDecimal;

public class PaymentProcessorImpl implements PaymentProcessor {
    static final String EXCEPTIONTEXT = "Account not found";
    private AccountService accountService;

    public PaymentProcessorImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    public boolean makeTransfer(Agreement source, Agreement destination, int sourceType,
                                int destinationType, BigDecimal amount) {

        Account sourceAccount = accountService.getAccounts(source).stream()
                .filter(account -> account.getType() == sourceType)
                .findAny()
                .orElseThrow(() -> new AccountException(EXCEPTIONTEXT));

        Account destinationAccount = accountService.getAccounts(destination).stream()
                .filter(account -> account.getType() == destinationType)
                .findAny()
                .orElseThrow(() -> new AccountException(EXCEPTIONTEXT));

        return accountService.makeTransfer(sourceAccount.getId(), destinationAccount.getId(), amount);
    }

    @Override
    public boolean makeTransferWithComission(Agreement source, Agreement destination,
                                             int sourceType, int destinationType,
                                             BigDecimal amount,
                                             BigDecimal commissionPercent) {

        Account sourceAccount = accountService.getAccounts(source).stream()
                .filter(account -> account.getType() == sourceType)
                .findAny()
                .orElseThrow(() -> new AccountException(EXCEPTIONTEXT));

        Account destinationAccount = accountService.getAccounts(destination).stream()
                .filter(account -> account.getType() == destinationType)
                .findAny()
                .orElseThrow(() -> new AccountException(EXCEPTIONTEXT));

        accountService.charge(sourceAccount.getId(), amount.negate().multiply(commissionPercent));

        return accountService.makeTransfer(sourceAccount.getId(), destinationAccount.getId(), amount);
    }
}
