package ru.otus.java.pro.jpql.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.jpql.entities.Client;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClientDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDao.class);
    private final EntityManager entityManager;

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            clients = entityManager
                    .createQuery("SELECT c FROM Client c", Client.class)
                    .getResultList();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("The error occurred when reading in client records from database", e);
        }
        return clients;
    }
}
