package ru.otus.java.pro.jdbcpro.datasource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
@RequiredArgsConstructor
public class DataSource {
    private static final Logger logger = LogManager.getLogger(DataSource.class.getName());

    private final String url;
    private Connection connection;
    private Statement statement;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url);
        statement = connection.createStatement();
        logger.info("Connection to the database has been established: {}", url);
    }

    public void close() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error("The error occurred when trying to close statement");
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("The error occurred when trying to close connection");
            }
        }
        logger.info("Disconnected from the database");
    }
}
