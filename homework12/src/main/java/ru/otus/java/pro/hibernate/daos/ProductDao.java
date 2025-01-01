package ru.otus.java.pro.hibernate.daos;

import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.otus.java.pro.hibernate.entities.ClientProduct;
import ru.otus.java.pro.hibernate.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDao extends AbstractDao<Product> {
    private static final Logger logger = LogManager.getLogger(ProductDao.class.getName());
    private final SessionFactory sf;

    public ProductDao(SessionFactory sf) {
        super(Product.class, sf);
        this.sf = sf;
    }

    public List<ClientProduct> findClientsByProductId(Long productId) {
        List<ClientProduct> clientProductList = new ArrayList<>();
        Product productOrNull = this.findById(productId);
        if (productOrNull == null) {
            logger.warn("There is no product with id={} in the database", productId);
            return clientProductList;
        }
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            clientProductList.addAll(productOrNull.getClients());
            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("There is no clients associated with product with id={} in the database: {}",
                    productId, e.getMessage());
        }
        return clientProductList;
    }

    public Optional<Product> findProductByTitle(String title) {
        Optional<Product> productOptional = Optional.empty();
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            productOptional = session.bySimpleNaturalId(Product.class).loadOptional(title);
            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("There is no product with title={} in the database: {}", title, e.getMessage());
        }
        return productOptional;
    }
}
