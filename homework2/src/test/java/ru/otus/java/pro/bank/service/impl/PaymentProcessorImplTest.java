package ru.otus.java.pro.bank.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.java.pro.bank.entity.Account;
import ru.otus.java.pro.bank.entity.Agreement;
import ru.otus.java.pro.bank.service.AccountService;
import ru.otus.java.pro.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.otus.java.pro.bank.service.impl.PaymentProcessorImpl.EXCEPTIONTEXT;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorImplTest {
    private Agreement sourceAgreement;
    private Agreement destinationAgreement;
    private Account sourceAccount;
    private Account destinationAccount;
    private static BigDecimal srcAmount;
    private static BigDecimal destAmount;
    private static BigDecimal sum;
    private static BigDecimal commission;

    @Mock
    AccountService accountService;

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    @BeforeAll
    static void setup() {
        srcAmount = BigDecimal.valueOf(100);
        destAmount = BigDecimal.valueOf(0);
        sum = BigDecimal.valueOf(10);
        commission = BigDecimal.valueOf(0.1);
    }

    @BeforeEach
    void init() {
        sourceAgreement = new Agreement();
        destinationAgreement = new Agreement();

        sourceAccount = new Account();
        destinationAccount = new Account();

        sourceAgreement.setId(1L);
        destinationAgreement.setId(2L);

        sourceAccount.setAgreementId(sourceAgreement.getId());
        sourceAccount.setAmount(srcAmount);
        sourceAccount.setType(0);

        destinationAccount.setAgreementId(destinationAgreement.getId());
        destinationAccount.setAmount(destAmount);
        destinationAccount.setType(0);
    }

    @Test
    void makeTransferTestWithVerify() {
        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 2L))).thenReturn(List.of(destinationAccount));

        when(accountService.makeTransfer(any(), any(), eq(sum))).thenReturn(true);

        boolean result = paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, sum);

        assertTrue(result);
        verify(accountService).getAccounts(sourceAgreement);
        verify(accountService).getAccounts(destinationAgreement);
        verify(accountService).makeTransfer(any(), any(), eq(sum));
    }

    @Test
    void makeTransferWithCommissionTestWithVerify() {
        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 2L))).thenReturn(List.of(destinationAccount));

        when(accountService.makeTransfer(any(), any(), eq(sum))).thenReturn(true);

        boolean result = paymentProcessor.makeTransferWithComission(sourceAgreement, destinationAgreement,
                0, 0, sum, commission);

        assertTrue(result);
        verify(accountService).getAccounts(sourceAgreement);
        verify(accountService).getAccounts(destinationAgreement);
        verify(accountService).charge(any(), eq(sum.negate().multiply(commission)));
        verify(accountService).makeTransfer(any(), any(), eq(sum));
    }

    @Test
    void makeTransferAccountNotFoundTest() {
        when(accountService.getAccounts(any())).thenReturn(Collections.emptyList());

        AccountException result = assertThrows(AccountException.class, () ->
                paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                        0, 0, sum));

        assertEquals(EXCEPTIONTEXT, result.getLocalizedMessage());
    }

    @Test
    void makeTransferWithCommissionAccountNotFoundTest() {
        when(accountService.getAccounts(any())).thenReturn(Collections.emptyList());

        AccountException result = assertThrows(AccountException.class, () ->
                paymentProcessor.makeTransferWithComission(sourceAgreement, destinationAgreement,
                        0, 0, sum, commission));

        assertEquals(EXCEPTIONTEXT, result.getLocalizedMessage());
    }
}
