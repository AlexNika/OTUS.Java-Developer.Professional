package ru.otus.java.pro.design_patterns.dao;

import net.datafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.design_patterns.entity.Coffee;
import ru.otus.java.pro.design_patterns.entity.SpecificCoffeeBuilder;
import ru.otus.java.pro.design_patterns.util.sqlQueries;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CoffeeItemsDao implements ItemsDao<Coffee> {
    private static final Logger logger = LogManager.getLogger(CoffeeItemsDao.class);

    private final Connection connection;
    private final List<Coffee> coffeeItems;

    public CoffeeItemsDao(Connection connection) {
        this.connection = connection;
        this.coffeeItems = new ArrayList<>();
    }

    @Override
    public void generateData(int itemsQuantity) {
        for (int i = 0; i < itemsQuantity; i++) {
            Faker faker = new Faker(new Random(i));
            Coffee coffee = new SpecificCoffeeBuilder()
                    .id(i)
                    .blendName(faker.coffee().blendName())
                    .body(faker.coffee().body())
                    .country(faker.coffee().country())
                    .intensifier(faker.coffee().intensifier())
                    .descriptor(faker.coffee().descriptor())
                    .variety(faker.coffee().variety())
                    .price(between(BigDecimal.valueOf(1000), BigDecimal.valueOf(3000)))
                    .build();
            this.coffeeItems.add(coffee);
        }
    }

    @Override
    public void createTable() {
        String sql = sqlQueries.CREATE.query;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.debug("SQL create table query executed: {}", sql);
        } catch (SQLException e) {
            logger.error("The error occurs while createTable sql query executed", e);
        }
    }

    @Override
    public List<Coffee> readData() {
        Coffee coffee;
        List<Coffee> coffeeItemsFromDb = new ArrayList<>();
        String sql = sqlQueries.READ.query;
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            logger.debug("SQL read query executed: {}", sql);
            while (rs.next()) {
                coffee = new Coffee(
                        Long.parseLong(rs.getString("id")),
                        rs.getString("blendName"),
                        rs.getString("body"),
                        rs.getString("country"),
                        rs.getString("intensifier"),
                        rs.getString("descriptor"),
                        rs.getString("variety"),
                        new BigDecimal(rs.getString("price"))
                );
                coffeeItemsFromDb.add(coffee);
            }
        } catch (SQLException e) {
            logger.error("The error occurs while select sql query executed", e);
        }
        return coffeeItemsFromDb;
    }

    @Override
    public int insertData(Coffee coffee) {
        int insertedItemsQty = 0;
        if (coffee == null) {
            return insertedItemsQty;
        }
        String sql = sqlQueries.INSERT.query;
        Object[] params = new Object[]{
                coffee.blendName(),
                coffee.body(),
                coffee.country(),
                coffee.intensifier(),
                coffee.descriptor(),
                coffee.variety(),
                coffee.price()
        };
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            insertedItemsQty = preparedStatement.executeUpdate();
            logger.debug("SQL insert query executed: {}", preparedStatement);
        } catch (SQLException e) {
            logger.error("The error occurs while update sql query executed", e);
        }
        return insertedItemsQty;
    }

    @Override
    public int updatePriceData(Coffee coffee, BigDecimal price) {
        int updatedItemsQty = 0;
        if (coffee == null) {
            return updatedItemsQty;
        }
        String sql = sqlQueries.UPDATE.query;
        Object[] params = new Object[]{
                price,
                coffee.id()
        };
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            updatedItemsQty = preparedStatement.executeUpdate();
            logger.debug("SQL update query executed: {}", preparedStatement);
        } catch (SQLException e) {
            logger.error("The error occurs while update sql query executed", e);
        }
        return updatedItemsQty;
    }

    @Override
    public List<Coffee> getGeneratedData() {
        return Collections.unmodifiableList(coffeeItems);
    }

    private @NotNull BigDecimal between(@NotNull BigDecimal leftEdge, @NotNull BigDecimal rightEdge) {
        int digitCount = Math.max(leftEdge.precision(), rightEdge.precision());
        int bitCount = (int) (digitCount / Math.log10(2.0));
        BigDecimal alpha = new BigDecimal(
                new BigInteger(bitCount, new Random())
        ).movePointLeft(digitCount);
        return leftEdge.add(rightEdge.subtract(leftEdge).multiply(alpha, new MathContext(digitCount)));
    }
}
