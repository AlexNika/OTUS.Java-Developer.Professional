package ru.otus.java.pro.bank.service;

import ru.otus.java.pro.bank.entity.Agreement;

import java.util.Optional;

public interface AgreementService {
    Agreement addAgreement(String name);

    Optional<Agreement> findByName(String name);
}
