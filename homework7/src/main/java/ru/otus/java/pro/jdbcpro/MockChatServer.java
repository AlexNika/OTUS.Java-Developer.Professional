package ru.otus.java.pro.jdbcpro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.jdbcpro.datasource.DataSource;
import ru.otus.java.pro.jdbcpro.datasource.DbMigrator;
import ru.otus.java.pro.jdbcpro.entities.User;
import ru.otus.java.pro.jdbcpro.exceptions.ORMException;
import ru.otus.java.pro.jdbcpro.repositories.AbstractRepository;

import java.sql.SQLException;

public class MockChatServer {
    private static final Logger logger = LogManager.getLogger(MockChatServer.class.getName());

    public static void main(String[] args) {
        DataSource dataSource = null;
        try {
            logger.info("Mock chat server started");
            dataSource = new DataSource("jdbc:h2:file:./homework7/src/main/resources/db;MODE=PostgreSQL");
            dataSource.connect();

            DbMigrator dbMigrator = new DbMigrator(dataSource, "./dbinit.sql");
            dbMigrator.run();

            AbstractRepository<User> usersRepository = new AbstractRepository<>(dataSource, User.class);

            usersRepository.save(new User("User1", "Password1", "NickName1"));
            usersRepository.save(new User("User2", "Password2", "NickName2"));
            usersRepository.save(new User("User3", "Password3", "NickName3"));
            logger.info(usersRepository.findById(2L));
            usersRepository.update(new User(3L, "User3.1", "Password3.1", "Nickname3.1"));
            usersRepository.save(new User("User4", "Password4", "NickName4"));
            logger.info(usersRepository.findAll());
            usersRepository.deleteById(3L);
            logger.info(usersRepository.findAll());
            usersRepository.deleteAll();
            logger.info(usersRepository.findAll());
            usersRepository.save(new User("User5", "Password5", "NickName5"));
            logger.info(usersRepository.findAll());
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when trying to operate with AbstractRepository");
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
            logger.info("Mock chat server finished");
        }
    }
}
