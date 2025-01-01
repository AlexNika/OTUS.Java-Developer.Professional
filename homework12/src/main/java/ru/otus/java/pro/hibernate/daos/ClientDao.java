package ru.otus.java.pro.hibernate.daos;

import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.otus.java.pro.hibernate.entities.Client;
import ru.otus.java.pro.hibernate.entities.ClientProduct;

import java.util.ArrayList;
import java.util.List;

public class ClientDao extends AbstractDao<Client> {
    private static final Logger logger = LogManager.getLogger(ClientDao.class.getName());
    private final SessionFactory sf;

    public ClientDao(SessionFactory sf) {
        super(Client.class, sf);
        this.sf = sf;
    }

    public List<ClientProduct> findProductsByClientId(Long clientId) {
        List<ClientProduct> clientProductList = new ArrayList<>();
        Client clientOrNull = this.findById(clientId);
        if (clientOrNull == null) {
            logger.warn("There is no client with id={} in the database", clientId);
            return clientProductList;
        }
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            clientProductList.addAll(clientOrNull.getProducts());
            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("There is no products associated with client with id={} in the database: {}",
                    clientId, e.getMessage());
        }
        return clientProductList;
    }
}
