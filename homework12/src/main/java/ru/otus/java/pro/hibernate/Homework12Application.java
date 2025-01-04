package ru.otus.java.pro.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import ru.otus.java.pro.hibernate.config.HConfig;
import ru.otus.java.pro.hibernate.daos.ClientDao;
import ru.otus.java.pro.hibernate.daos.ProductDao;

public class Homework12Application {
    private static final Logger logger = LogManager.getLogger(Homework12Application.class.getName());

    public static void main(String[] args) {
        logger.info("Homework12Application.main started");
        try (SessionFactory sf = HConfig.getSessionFactory()) {
            DataStuffer dataStuffer = new DataStuffer(sf);
            dataStuffer.populateClients();
            dataStuffer.populateProducts();
            dataStuffer.doShopping();

            ClientDao clientDao = new ClientDao(sf);
            ProductDao productDao = new ProductDao(sf);
            Console console = new Console(clientDao, productDao);
            console.start();
        } catch (ExceptionInInitializerError e) {
            logger.error("Initial SessionFactory creation failed", e);
        }
        logger.info("Homework12Application.main finished");
    }
}
