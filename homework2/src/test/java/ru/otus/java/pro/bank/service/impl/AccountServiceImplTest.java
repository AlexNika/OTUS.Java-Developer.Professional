package ru.otus.java.pro.bank.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.java.pro.bank.dao.AccountDao;
import ru.otus.java.pro.bank.entity.Account;
import ru.otus.java.pro.bank.entity.Agreement;
import ru.otus.java.pro.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    private Account sourceAccount;
    private Account destinationAccount;
    private static BigDecimal srcAmount;
    private static BigDecimal destAmount;
    private static BigDecimal sum;

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @BeforeAll
    static void setup() {
        srcAmount = BigDecimal.valueOf(100);
        destAmount = BigDecimal.valueOf(0);
        sum = BigDecimal.valueOf(10);
    }

    @Nested
    class nestedTestBlock {

        @BeforeEach
        void init() {
            sourceAccount = new Account();
            sourceAccount.setAmount(srcAmount);

            destinationAccount = new Account();
            destinationAccount.setAmount(destAmount);

            lenient().when(accountDao.findById(1L)).thenReturn(Optional.of(sourceAccount));
            lenient().when(accountDao.findById(2L)).thenReturn(Optional.of(destinationAccount));
        }

        @Test
        void makeTransferTest() {
            accountServiceImpl.makeTransfer(1L, 2L, sum);

            assertEquals(srcAmount.subtract(sum), sourceAccount.getAmount());
            assertEquals(destAmount.add(sum), destinationAccount.getAmount());
        }

        @Test
        void makeTransferReturnTrueTest() {
            boolean result = accountServiceImpl.makeTransfer(1L, 2L, sum);
            assertTrue(result);
        }

        @Test
        void makeTransferReturnFalseTest() {
            boolean result = accountServiceImpl.makeTransfer(1L, 2L, srcAmount.add(sum));
            assertFalse(result);
        }

        @Test
        void makeTransferTestWithVerify() {
            sourceAccount.setId(1L);
            destinationAccount.setId(2L);

            ArgumentMatcher<Account> sourceMatcher =
                    argument -> argument.getId().equals(1L) && argument.getAmount().equals(srcAmount.subtract(sum));

            ArgumentMatcher<Account> destinationMatcher =
                    argument -> argument.getId().equals(2L) && argument.getAmount().equals(destAmount.add(sum));

            accountServiceImpl.makeTransfer(1L, 2L, sum);

            verify(accountDao).save(argThat(sourceMatcher));
            verify(accountDao).save(argThat(destinationMatcher));
        }

        @Test
        void makeTransferTestValues() {
            ArgumentCaptor<Account> savedAccounts = ArgumentCaptor.captor();
            when(accountDao.save(savedAccounts.capture())).thenReturn(sourceAccount);

            boolean result = accountServiceImpl.makeTransfer(1L, 2L, sum);

            assertTrue(result);
            assertEquals(srcAmount.subtract(sum), savedAccounts.getAllValues().get(0).getAmount());
            assertEquals(destAmount.add(sum), savedAccounts.getAllValues().get(1).getAmount());
        }

        @Test
        void chargeReturnTrueTest() {
            boolean result = accountServiceImpl.charge(1L, sum);
            assertTrue(result);
        }

        @Test
        void chargeTestValuesWithVerify() {
            sourceAccount.setId(1L);
            destinationAccount.setId(2L);

            accountServiceImpl.charge(1L, sum);
            accountServiceImpl.charge(2L, sum);

            assertEquals(srcAmount.subtract(sum), sourceAccount.getAmount());
            assertEquals(destAmount.subtract(sum), destinationAccount.getAmount());
            verify(accountDao).findById(1L);
            verify(accountDao).findById(2L);
            verify(accountDao).save(sourceAccount);
            verify(accountDao).save(destinationAccount);
        }

        @Test
        void getAccountsTestWithVerify() {
            sourceAccount.setId(1L);
            destinationAccount.setId(2L);

            List<Account> expectedAccounts = new ArrayList<>();
            expectedAccounts.add(sourceAccount);
            expectedAccounts.add(destinationAccount);
            when(accountDao.findAll()).thenReturn(expectedAccounts);

            List<Account> actualAccounts = accountServiceImpl.getAccounts();

            assertArrayEquals(expectedAccounts.toArray(), actualAccounts.toArray());
            verify(accountDao).findAll();
        }

        @Test
        void getAccountsByAgreementTestWithVerify() {
            Agreement agreement = new Agreement(1L, "Agreement1");
            sourceAccount.setAgreementId(agreement.getId());
            sourceAccount.setId(1L);

            List<Account> expectedAccountsList = new ArrayList<>();
            expectedAccountsList.add(sourceAccount);
            when(accountDao.findByAgreementId(agreement.getId())).thenReturn(expectedAccountsList);

            List<Account> actualAccountsList = accountServiceImpl.getAccounts(agreement);

            assertArrayEquals(expectedAccountsList.toArray(), actualAccountsList.toArray());
            verify(accountDao).findByAgreementId(agreement.getId());
        }
    }

    @Test
    void addAccountTestWithVerify() {
        Agreement agreement = new Agreement(1L, "Agreement1");

        BigDecimal accountAmount = srcAmount;
        Integer accountType = 42;
        String accountNumber = "1";

        Account account = new Account(1L, accountAmount, accountType, accountNumber, agreement.getId());

        ArgumentMatcher<Account> accountArgumentMatcher = argument ->
                argument != null && accountNumber.equals(account.getNumber())
                        && accountType.equals(account.getType())
                        && srcAmount.equals(account.getAmount());
        when(accountDao.save(argThat(accountArgumentMatcher))).thenReturn(account);
        accountServiceImpl.addAccount(agreement, accountNumber, accountType, accountAmount);

        verify(accountDao).save(argThat(accountArgumentMatcher));
    }

    @Test
    void makeTransferSourceNotFoundTestWithVerify() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class,
                () -> accountServiceImpl.makeTransfer(1L, 2L, sum));

        assertEquals("No source account", result.getLocalizedMessage());
        verify(accountDao, never()).save(any());
    }

    @Test
    void chargeSourceNotFoundTestWithVerify() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());
        AccountException result = assertThrows(AccountException.class,
                () -> accountServiceImpl.charge(1L, sum));

        assertEquals("No source account", result.getLocalizedMessage());
        verify(accountDao, never()).save(any());
    }

    @Test
    void iterableToListTest() {
        assertEquals(0, accountServiceImpl.getAccounts().size());
    }
}
