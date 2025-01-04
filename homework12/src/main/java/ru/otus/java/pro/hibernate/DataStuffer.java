package ru.otus.java.pro.hibernate;

import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.Status;
import ru.otus.java.pro.hibernate.daos.ClientDao;
import ru.otus.java.pro.hibernate.daos.ProductDao;
import ru.otus.java.pro.hibernate.entities.Client;
import ru.otus.java.pro.hibernate.entities.Product;

import java.math.BigDecimal;

public class DataStuffer {
    private static final Logger logger = LogManager.getLogger(DataStuffer.class.getName());

    private final SessionFactory sf;

    public DataStuffer(SessionFactory sf) {
        this.sf = sf;
    }

    public void populateClients() {
        ClientDao clientDao = new ClientDao(sf);
        final Client client1 = new Client("Александр", Status.MANAGED);
        final Client client2 = new Client("Иван", Status.MANAGED);
        final Client client3 = new Client("Василий", Status.MANAGED);
        final Client client4 = new Client("Вадим", Status.MANAGED);

        clientDao.saveOrUpdate(client1);
        clientDao.saveOrUpdate(client2);
        clientDao.saveOrUpdate(client3);
        clientDao.saveOrUpdate(client4);
    }

    public void populateProducts() {
        ProductDao productDao = new ProductDao(sf);
        final Product product1 = new Product("Молоко", BigDecimal.valueOf(130), Status.MANAGED);
        final Product product2 = new Product("Хлеб", BigDecimal.valueOf(70), Status.MANAGED);
        final Product product3 = new Product("Сыр", BigDecimal.valueOf(400), Status.MANAGED);
        final Product product4 = new Product("Торт", BigDecimal.valueOf(900), Status.MANAGED);

        productDao.saveOrUpdate(product1);
        productDao.saveOrUpdate(product2);
        productDao.saveOrUpdate(product3);
        productDao.saveOrUpdate(product4);
    }

    public void doShopping() {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Client client1 = session.get(Client.class, 1);
            Client client2 = session.get(Client.class, 2);
            Client client3 = session.get(Client.class, 3);
            Client client4 = session.get(Client.class, 4);

            Product product1 = session.get(Product.class, 1);
            Product product2 = session.get(Product.class, 2);
            Product product3 = session.get(Product.class, 3);
            Product product4 = session.get(Product.class, 4);

            client1.addProduct(product1);
            client1.addProduct(product2);
            client1.addProduct(product3);
            client1.addProduct(product4);

            client2.addProduct(product2);
            client2.addProduct(product3);

            client3.addProduct(product2);
            client3.addProduct(product3);
            client3.addProduct(product4);

            client4.addProduct(product1);
            client4.addProduct(product2);
            client4.addProduct(product3);
            client4.addProduct(product4);

            client4.removeProduct(product2);
            client4.removeProduct(product3);

            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("The error occurs when trying to get entity instance " +
                    "or fill many2many staging table: {}", e.getMessage());
        }
    }
}
