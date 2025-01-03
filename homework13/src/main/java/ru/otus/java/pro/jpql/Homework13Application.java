package ru.otus.java.pro.jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.jpql.daos.ClientDao;
import ru.otus.java.pro.jpql.entities.Client;
import ru.otus.java.pro.jpql.util.DataInitializer;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class Homework13Application {
    private static final String CLASSNAME = MethodHandles.lookup().lookupClass().getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASSNAME);

    private static final String PERSISTENCE_UNIT_NAME = "HW13";

    public static void main(String[] args) {
        LOGGER.info("{} started", CLASSNAME);

        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)) {
            try (EntityManager entityManager = emf.createEntityManager()) {
                DataInitializer dataInitializer = new DataInitializer(entityManager);
                dataInitializer.start();

                ClientDao clientDao = new ClientDao(entityManager);
                List<Client> clients = clientDao.findAll();
                clients.forEach(c -> LOGGER.info("{}", c));
            }
        } catch (IllegalArgumentException | PersistenceException e) {
            LOGGER.error("Something went wrong when running application {}", CLASSNAME, e);
        }
        LOGGER.info("{} finished", CLASSNAME);
    }
}
