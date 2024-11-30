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

    private final CoffeeItemsDao coffeeItemsDao;

    public CoffeeItemsService(Connection connection) {
        this.coffeeItemsDao = new CoffeeItemsDao(connection);
    }

    @Override
    public void populateData(int qty) throws SQLException {
        int insertedItemsQty = 0;
        List<Coffee> coffeeItems;
        coffeeItemsDao.createTable();
        coffeeItemsDao.generateData(qty);
        coffeeItems = coffeeItemsDao.getGeneratedData();
        for (Coffee coffeeItem : coffeeItems) {
            insertedItemsQty += coffeeItemsDao.insertData(coffeeItem);
        }
        logger.info("Rows inserted into the database table: {}", insertedItemsQty);
    }

    @Override
    public void updateDataPrice() throws SQLException {
        int updatedItemsQty = 0;
        List<Coffee> coffeeItemsFromDB = coffeeItemsDao.readData();
        for (Coffee coffeeItem : coffeeItemsFromDB) {
            BigDecimal price = coffeeItem.price().multiply(BigDecimal.valueOf(2));
            updatedItemsQty += coffeeItemsDao.updatePriceData(coffeeItem, price);
        }
        logger.info("Rows updated in the database table: {}", updatedItemsQty);
    }
}
