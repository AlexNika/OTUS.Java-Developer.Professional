package ru.otus.java.pro.bank.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.java.pro.bank.dao.AgreementDao;
import ru.otus.java.pro.bank.entity.Agreement;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgreementServiceImplTest {
    private static String agreementName;

    @Mock
    private AgreementDao agreementDao;

    @InjectMocks
    AgreementServiceImpl agreementServiceImpl;

    @BeforeAll
    static void setup() {
        agreementName = "Agreement1";
    }

    @BeforeEach
    public void init() {
        agreementServiceImpl = new AgreementServiceImpl(agreementDao);
    }

    @Test
    void findByNameTest() {
        Agreement agreement = new Agreement(10L, agreementName);

        when(agreementDao.findByName(agreementName)).thenReturn(Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(agreementName);

        assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    @Test
    void findByNameTestWithCaptor() {
        Agreement agreement = new Agreement(10L, agreementName);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(agreementDao.findByName(captor.capture())).thenReturn(Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(agreementName);

        assertEquals(agreementName, captor.getValue());
        assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    @Test
    void findByNameNullTestWithVerify() {
        when(agreementDao.findByName(agreementName)).thenReturn(null);

        assertNull(agreementServiceImpl.findByName(agreementName));
        verify(agreementDao).findByName(agreementName);
    }

    @Test
    void addAgreementTestValues() {
        Agreement expextedAgreement = new Agreement(1L, agreementName);

        ArgumentCaptor<Agreement> captor = ArgumentCaptor.forClass(Agreement.class);
        when(agreementDao.save(captor.capture())).thenReturn(expextedAgreement);

        Agreement actualAgreement = agreementServiceImpl.addAgreement(agreementName);

        assertEquals(agreementName, captor.getValue().getName());
        assertEquals(expextedAgreement.getId(), actualAgreement.getId());
        assertEquals(expextedAgreement.getName(), actualAgreement.getName());
    }

    @Test
    void addAgreementWithVerify() {
        ArgumentMatcher<Agreement> agreementMatcher = argument -> argument.getName().equals(agreementName);

        agreementServiceImpl.addAgreement(agreementName);

        verify(agreementDao).save(argThat(agreementMatcher));
    }
}
