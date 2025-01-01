package ru.otus.java.pro.hibernate.daos;

import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

@AllArgsConstructor
public abstract class AbstractDao<T> {
    private static final Logger logger = LogManager.getLogger(AbstractDao.class.getName());
    private Class<T> cls;
    private final SessionFactory sf;

    public List<T> findAll() {
        List<T> result = null;
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            result = session.createQuery("from " + cls.getSimpleName(), cls)
                    .getResultList();
            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("The error occurs when trying to fetch all entity's instances from database: {}",
                    e.getMessage());
        }
        return result;
    }

    public T findById(Long id) {
        T result = null;
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            result = session.get(cls, id);
            session.getTransaction().commit();
            return result;
        } catch (PersistenceException e) {
            logger.error("There is no entity instance with id={} in the database: {}",
                    id, e.getMessage());
        }
        return result;
    }

    public T saveOrUpdate(T obj) {
        if (obj == null) {
            logger.warn("Can't ave or update null object");
            return null;
        }
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            session.merge(obj);
            session.getTransaction().commit();
        } catch (PersistenceException e) {
            logger.error("The error occurs when trying to save/update entity instance to database: {}",
                    e.getMessage());
        }
        return obj;
    }

    public void deleteObject(T obj) {
        if (obj == null) {
            logger.warn("Can't deleteObject null object");
            return;
        }
        try (Session session = sf.getCurrentSession()) {
            session.beginTransaction();
            session.remove(obj);
            session.getTransaction().commit();
        }
    }
}
