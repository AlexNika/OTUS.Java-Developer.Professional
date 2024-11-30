package ru.otus.java.pro.design_patterns.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.design_patterns.entity.Coffee;

import java.sql.Connection;
import java.sql.SQLException;

public class CoffeeItemsServiceProxy implements ItemsService<Coffee> {
    private static final Logger logger = LogManager.getLogger(CoffeeItemsServiceProxy.class);

    private final Connection connection;
    private final CoffeeItemsService coffeeItemsService;

    public CoffeeItemsServiceProxy(Connection connection) {
        this.connection = connection;
        coffeeItemsService = new CoffeeItemsService(connection);
    }

    @Override
    public void populateData(int qty) throws SQLException {
        logger.info("Proxy pattern applied. " +
                "Method populateData({}) was launched from the class CoffeeItemsServiceProxy", qty);
        try {
            connection.setAutoCommit(false);
            coffeeItemsService.populateData(qty);
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error. Transaction 'populateData' cannot be completed. Transaction rollback", e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void updateDataPrice() throws SQLException {
        logger.info("Proxy pattern applied. " +
                "Method updateDataPrice() was launched from the class CoffeeItemsServiceProxy");
        try {
            connection.setAutoCommit(false);
            coffeeItemsService.updateDataPrice();
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Error. Transaction 'updateDataPrice' cannot be completed. Transaction rollback", e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
