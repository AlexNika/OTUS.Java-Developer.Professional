package ru.otus.java.pro.jdbcpro.datasource;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.jdbcpro.exceptions.ORMException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@NoArgsConstructor(force = true)
@AllArgsConstructor
public class DbMigrator implements Runnable {
    private static final Logger logger = LogManager.getLogger(DbMigrator.class.getName());

    private final DataSource dataSource;
    private String sqlScriptsFileName;

    public DbMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run() {
        logger.info("Method 'run' started");
        List<String> sqlScripts;
        Optional<List<String>> sqlScriptsOptional = readSqlScriptsFile();
        if (sqlScriptsOptional.isPresent()) {
            boolean isMigrationTableExists = tableExist("MIGRATIONS");
            logger.debug("Migration table exists: {}", isMigrationTableExists);
            int skipIndex = 0;
            sqlScripts = sqlScriptsOptional.get();
            if (isMigrationTableExists) skipIndex = 1;
            sqlScripts.subList(skipIndex, sqlScripts.size()).forEach(sql -> {
                try {
                    init(sql);
                } catch (SQLException e) {
                    logger.error("Sql script can't be executed, {}: ", sql);
                }
            });
        } else {
            throw new ORMException("Can't make initial migration. MockChatServer is stopped!");
        }
        logger.info("Method 'run' finished");
    }

    private Optional<List<String>> readSqlScriptsFile() {
        logger.info("Method 'readSqlScriptsFile' started");
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(sqlScriptsFileName)) {
            if (inputStream != null) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                     BufferedReader br = new BufferedReader(inputStreamReader)) {
                    Stream<String> lines = br.lines();
                    logger.debug("SQL Scripts file has been read");
                    logger.info("Method 'readSqlScriptsFile' finished");
                    return Optional.of(lines.toList());
                }
            } else {
                logger.error("SQL init file {} not found in resources folder", sqlScriptsFileName);
            }
        } catch (IOException e) {
            logger.error("General I/O exception. Can't read sqlScript init file", e);
        }
        return Optional.empty();
    }

    private void init(String sql) throws SQLException {
        logger.info("Method 'init' started");
        if (sql == null || sql.isEmpty()) {
            throw new ORMException("Can't execute initial migration. Sql script is null or empty");
        }
        if (dataSource != null) {
            dataSource.getStatement().executeUpdate(sql);
            logger.debug("Sql script initiated: {}", sql);
        }
        logger.info("Method 'init' finished");
    }

    public boolean tableExist(String tableName) {
        logger.info("Method 'tableExist' started");
        boolean tExists = false;
        if (dataSource != null) {
            Connection connection = dataSource.getConnection();
            try (ResultSet rs = connection.getMetaData().getTables(null, "PUBLIC", tableName,
                    new String[]{"TABLE", "VIEW"})) {
                while (rs.next()) {
                    String tName = rs.getString("TABLE_NAME");
                    if (tName != null && tName.equals(tableName)) {
                        tExists = true;
                        logger.debug("Migration table '{}' exists", tableName);
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new ORMException("Can't check if table '" + tableName + "' exists.");
            }
        }
        logger.info("Method 'tableExist' finished");
        return tExists;
    }
}
