package ru.otus.java.pro.hibernate.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.java.pro.hibernate.entities.Client;
import ru.otus.java.pro.hibernate.entities.ClientProduct;
import ru.otus.java.pro.hibernate.entities.Product;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HConfig {

    public static SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

        configuration.addAnnotatedClass(Client.class);
        configuration.addAnnotatedClass(Product.class);
        configuration.addAnnotatedClass(ClientProduct.class);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        return configuration.buildSessionFactory(builder.build());
    }
}
