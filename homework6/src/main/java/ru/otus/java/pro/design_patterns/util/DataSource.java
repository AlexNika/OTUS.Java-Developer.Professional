package ru.otus.java.pro.design_patterns.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.PropertyResourceBundle;

import static java.sql.DriverManager.getConnection;

public final class DataSource {
    private static final Logger logger = LogManager.getLogger(DataSource.class);

    private static DataSource dataSource = null;
    private static Connection connection = null;
    private static final StringBuilder connectionString = new StringBuilder("jdbc:");

    private String username = "";
    private String password = "";
    private String driver = "";

    public DataSource() {
        try (InputStream fileInputStream = new FileInputStream("homework6/src/main/resources/database.properties")) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            PropertyResourceBundle bundle = new PropertyResourceBundle(bufferedInputStream);
            this.driver = bundle.getString("driver");
            this.username = bundle.getString("username");
            this.password = bundle.getString("password");
            String host = bundle.getString("host");
            String database = bundle.getString("database");
            String port = bundle.getString("port");
            DataSource.connectionString.append(toLowerCase(JdbcDrivers.valueOf(driver)));
            DataSource.connectionString
                    .append("://")
                    .append(host)
                    .append(":")
                    .append(port)
                    .append("/")
                    .append(database)
                    .append("?useUnicode=true&characterEncoding=UTF-8");
            logger.debug("connectionString: {}", DataSource.connectionString);
        } catch (IOException e) {
            logger.error("The error occurs when trying to read 'database.properties' file", e);
        }
    }

    public static DataSource createInstance() {
        try {
            if (dataSource == null) {
                logger.info("Singleton pattern applied. Datasource instance created.");
                dataSource = new DataSource();
                dataSource.initDB();
            }
        } catch (ClassNotFoundException e) {
            logger.error("The error occurs when trying to createTable datasource instance", e);
        }
        return dataSource;
    }

    public static Connection getDBConnection(String username, String password) {
        try {
            DataSource.connection = getConnection(String.valueOf(DataSource.connectionString), username, password);
        } catch (SQLException e) {
            logger.error("The error occurs when trying to get connection to the database", e);
        }
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void initDB() throws ClassNotFoundException {
        String jdbcDriver = JdbcDrivers.valueOf(driver).getDriverName();
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            logger.error("The error occurs when method tries to load in a class through its string name", e);
        }
    }

    private static String toLowerCase(Enum<?> aEnum) {
        if (aEnum == null) {
            return null;
        }
        return toLowerCase(aEnum.name());
    }

    private static String toLowerCase(String message) {
        if (message == null) {
            return null;
        }
        return message.toLowerCase();
    }
}
