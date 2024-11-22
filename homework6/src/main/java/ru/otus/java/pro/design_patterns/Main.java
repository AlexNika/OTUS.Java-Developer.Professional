package ru.otus.java.pro.design_patterns;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.design_patterns.service.CoffeeItemsServiceProxy;
import ru.otus.java.pro.design_patterns.util.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String... args) {
        DataSource dataSource = DataSource.createInstance();
        try (Connection connection = DataSource.getDBConnection(dataSource.getUsername(), dataSource.getPassword())) {
            CoffeeItemsServiceProxy coffeeItemsServiceProxy = new CoffeeItemsServiceProxy(connection);
            coffeeItemsServiceProxy.populateData(100);
            coffeeItemsServiceProxy.updateDataPrice();
        } catch (SQLException e) {
            logger.error("The error occurs when trying to get connection to the database", e);
        }
    }
}