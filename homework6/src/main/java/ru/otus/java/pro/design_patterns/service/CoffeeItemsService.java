package ru.otus.java.pro.design_patterns.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.design_patterns.dao.CoffeeItemsDao;
import ru.otus.java.pro.design_patterns.entity.Coffee;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CoffeeItemsService implements ItemsService<Coffee> {
    private static final Logger logger = LogManager.getLogger(CoffeeItemsService.class);

    private final Connection connection;
    private final CoffeeItemsDao coffeeItemsDao;

    public CoffeeItemsService(Connection connection) {
        this.connection = connection;
        this.coffeeItemsDao = new CoffeeItemsDao(this.connection);
    }

    @Override
    public void populateData(int qty) throws SQLException {
        int insertedItemsQty = 0;
        List<Coffee> coffeeItems;
        coffeeItemsDao.createTable();
        coffeeItemsDao.generateData(qty);
        coffeeItems = coffeeItemsDao.getGeneratedData();
        try {
            connection.setAutoCommit(false);
            for (Coffee coffeeItem : coffeeItems) {
                insertedItemsQty += coffeeItemsDao.insertData(coffeeItem);
            }
            connection.commit();
            logger.info("Rows inserted into the database table: {}", insertedItemsQty);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void updateDataPrice() throws SQLException {
        int updatedItemsQty = 0;
        try {
            connection.setAutoCommit(false);
            List<Coffee> coffeeItemsFromDB = coffeeItemsDao.readData();
            for (Coffee coffeeItem : coffeeItemsFromDB) {
                BigDecimal price = coffeeItem.price().multiply(BigDecimal.valueOf(2));
                updatedItemsQty += coffeeItemsDao.updatePriceData(coffeeItem, price);
            }
            connection.commit();
            logger.info("Rows updated in the database table: {}", updatedItemsQty);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
