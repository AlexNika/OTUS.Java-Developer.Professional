package ru.otus.java.pro.jdbcpro.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryIdField;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryTable;
import ru.otus.java.pro.jdbcpro.datasource.DataSource;
import ru.otus.java.pro.jdbcpro.datasource.DbMigrator;
import ru.otus.java.pro.jdbcpro.entities.FieldWrapper;
import ru.otus.java.pro.jdbcpro.exceptions.ORMException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AbstractRepository<T> {
    private static final Logger logger = LogManager.getLogger(AbstractRepository.class.getName());

    private final DataSource dataSource;
    private final Class<T> cls;

    private String tableName = null;
    private List<FieldWrapper<T>> wrappedFieldList;
    private PreparedStatement psInsert;
    private PreparedStatement psUpdate;
    private PreparedStatement psFindById;
    private PreparedStatement psFindAll;
    private PreparedStatement psDeleteById;
    private PreparedStatement psDeleteAll;

    public AbstractRepository(DataSource dataSource, Class<T> cls) {
        logger.info("Constructor 'AbstractRepository' build started");
        this.dataSource = dataSource;
        this.cls = cls;

        this.checkClassForNull();
        this.wrapFields();
        this.validateClass();
        this.prepareInsert();
        this.prepareUpdate();
        this.prepareFindById();
        this.prepareFindAll();
        this.prepareDeleteById();
        this.prepareDeleteAll();
        logger.info("Constructor 'AbstractRepository' build finished");
    }

    public void save(T entity) {
        logger.info("Method 'Save' started, entity: {}", entity);
        List<Field> entityFieldList = List.of(entity.getClass().getDeclaredFields());
        try {
            for (int i = 0; i < wrappedFieldList.size() - 1; i++) {
                if (entityFieldList.contains(wrappedFieldList.get(i).getField()) &&
                        entityFieldList.get(i) != null) {
                    psInsert.setObject(i + 1,
                            wrappedFieldList.get(i).getGetter().invoke(entity));
                } else {
                    throw new ORMException("The entity object is not valid. Not all required fields are set ");
                }
            }
            logger.debug("psInsert: {}", psInsert);
            psInsert.executeUpdate();
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when saving entity to DB: " + entity);
        }
        logger.info("Method 'Save' finished");
    }

    public void update(T entity) {
        logger.info("Method 'Update' started, entity: {}", entity);
        List<Field> entityFieldList = List.of(entity.getClass().getDeclaredFields());
        try {
            for (int i = 0; i < wrappedFieldList.size(); i++) {
                if (entityFieldList.contains(wrappedFieldList.get(i).getField()) &&
                        entityFieldList.get(i) != null) {
                    psUpdate.setObject(i + 1,
                            wrappedFieldList.get(i).getGetter().invoke(entity));
                } else {
                    throw new ORMException("The entity object is not valid. Not all required fields are set ");
                }
            }
            logger.debug("psUpdate: {}", psUpdate);
            int result = psUpdate.executeUpdate();
            logger.debug("Updated: {} records in DB", result);
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when creating update prepare statement");
        }
        logger.info("Method 'Update' finished");
    }

    public T findById(Long id) {
        logger.info("Method 'findById' started, id: {}", id);
        T newEntity = null;
        try {
            psFindById.setLong(1, id);
            logger.debug("psFindById: {}", psFindById);
            try (ResultSet rs = psFindById.executeQuery()) {
                while (rs.next()) {
                    newEntity = createNewEntity();
                    T finalNewEntity = newEntity;
                    wrappedFieldList
                            .forEach(f -> {
                                try {
                                    f.getSetter().invoke(finalNewEntity, rs.getObject(f.getField().getName()));
                                } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
                                    logger.error(e.getMessage());
                                }
                            });
                }
                logger.debug("New entity: {}", newEntity);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when trying to find entity by id in DB: " + id);
        }
        logger.info("Method 'findById' finished");
        return newEntity;
    }

    public List<T> findAll() {
        logger.info("Method 'findAll' started");
        T newEntity;
        List<T> newEntitiesList = new ArrayList<>();
        logger.debug("psFindAll: {}", psFindAll);
        try (ResultSet rs = psFindAll.executeQuery()) {
            while (rs.next()) {
                newEntity = createNewEntity();
                T finalNewEntity = newEntity;
                wrappedFieldList
                        .forEach(f -> {
                            try {
                                f.getSetter().invoke(finalNewEntity, rs.getObject(f.getField().getName()));
                            } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
                                logger.error(e.getMessage());
                            }
                        });
                newEntitiesList.add(finalNewEntity);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when trying to find all entities in DB");
        }
        logger.info("Method 'findAll' finished");
        return Collections.unmodifiableList(newEntitiesList);
    }

    public void deleteById(Long id) {
        logger.info("Method 'deleteById' started, id: {}", id);
        try {
            psDeleteById.setLong(1, id);
            logger.debug("psDeleteById: {}", psDeleteById);
            int result = psDeleteById.executeUpdate();
            logger.debug("Deleted (by id): {} records from DB", result);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when trying to delete entity by id from DB: " + id);
        }
        logger.info("Method 'deleteById' finished");
    }

    public void deleteAll() {
        logger.info("Method 'deleteAll' started");
        try {
            logger.debug("psDeleteAll: {}", psDeleteAll);
            int result = psDeleteAll.executeUpdate();
            logger.debug("Deleted (all): {} records from DB", result);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("Something went wrong when trying to delete all entities from DB");
        }
        logger.info("Method 'deleteAll' finished");
    }

    private void wrapFields() {
        logger.info("Method 'wrapFields' started");
        wrappedFieldList = new ArrayList<>();
        for (Field f : List.of(cls.getDeclaredFields())) {
            FieldWrapper<T> fieldWrapper = new FieldWrapper<>(cls, f);
            wrappedFieldList.add(fieldWrapper);
        }
        wrappedFieldList = wrappedFieldList.stream()
                .sorted(Comparator.comparing(FieldWrapper<T>::isPkField))
                .toList();
        wrappedFieldList.forEach(logger::debug);
        logger.info("Method 'wrapFields' finished");
    }

    private void prepareInsert() {
        logger.info("Method 'prepareInsert' started");
        StringBuilder query = new StringBuilder("insert into ");
        query.append(tableName).append(" (");
        wrappedFieldList.stream()
                .filter(f -> !f.isPkField())
                .forEach(f -> query.append(f.getFieldName()).append(", "));
        query.setLength(query.length() - 2);
        query.append(") values (");
        wrappedFieldList.forEach(f -> {
            if (f.isPkField()) return;
            query.append("?, ");
        });
        query.setLength(query.length() - 2);
        query.append(");");
        logger.debug("INSERT query: {}", query);
        try {
            psInsert = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ORMException("INSERT: Failed to initialize the repository for the class " +
                    cls.getSimpleName());
        }
        logger.info("Method 'prepareInsert' finished");
    }

    private void prepareUpdate() {
        logger.info("Method 'prepareUpdate' started");
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName).append(" SET ");
        wrappedFieldList.stream()
                .filter(f -> !f.isPkField())
                .forEach(f -> query.append(f.getFieldName()).append(" = ?, "));
        query.setLength(query.length() - 2);
        FieldWrapper<T> pkFieldWrapper = wrappedFieldList.stream()
                .filter(FieldWrapper::isPkField)
                .findAny()
                .orElseThrow(() -> new ORMException("UPDATE: Can't find PK field. " +
                        "The entity is incorrectly marked up "));
        query.append(" WHERE ").append(pkFieldWrapper.getFieldName()).append(" = ?;");
        logger.debug("UPDATE query: {}", query);
        try {
            psUpdate = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ORMException("UPDATE: Failed to initialize the repository for the class " +
                    cls.getSimpleName());
        }
        logger.info("Method 'prepareUpdate' finished");
    }

    private void prepareFindById() {
        logger.info("Method 'prepareFindById' started");
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(" WHERE ");
        query.append(wrappedFieldList.get(wrappedFieldList.size() - 1).getFieldName()).append(" = ?;");
        logger.debug("SELECT (find by id) query: {}", query);
        try {
            psFindById = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ORMException("FINDBYID: Failed to initialize the repository for the class " +
                    cls.getSimpleName());
        }
        logger.info("Method 'prepareFindById' finished");
    }

    private void prepareFindAll() {
        logger.info("Method 'prepareFindAll' started");
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName).append(";");
        logger.debug("SELECT (find all) query: {}", query);
        try {
            psFindAll = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ORMException("FINDALL: Failed to initialize the repository for the class " +
                    cls.getSimpleName());
        }
        logger.info("Method 'prepareFindAll' finished");
    }

    private void prepareDeleteById() {
        logger.info("Method 'prepareDeleteById' started");
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName).append(" WHERE ");
        query.append(wrappedFieldList.get(wrappedFieldList.size() - 1).getFieldName()).append(" = ?;");
        logger.debug("DELETE (delete by id) query: {}", query);
        try {
            psDeleteById = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ORMException("DELETEBYID: Failed to initialize the repository for the class " +
                    cls.getSimpleName());
        }
        logger.info("Method 'prepareDeleteById' finished");
    }

    private void prepareDeleteAll() {
        logger.info("Method 'prepareDeleteAll' started");
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName).append(";");
        logger.debug("DELETE (delete all) query: {}", query);
        try {
            psDeleteAll = dataSource.getConnection().prepareStatement(query.toString());
        } catch (SQLException e) {
            throw new ORMException("DELETEALL: Failed to initialize the repository for the class " +
                    cls.getSimpleName());
        }
        logger.info("Method 'prepareDeleteAll' finished");
    }

    private T createNewEntity() {
        logger.info("Method 'createNewEntity' started");
        Constructor<T> constructor;
        try {
            constructor = cls.getConstructor();
            logger.debug("Constructor: {}", constructor.getName());
            logger.info("Method 'createNewEntity' finished");
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private void validateClass() {
        logger.info("Method 'validateClass' started");
        checkClassConstructors();
        checkClassAnnotations();
        checkTableNameValue();
        checkTablePresenceInDb();
        checkFieldsAnnotations();
        logger.info("Method 'validateClass' finished");
    }

    private void checkClassForNull() {
        logger.info("Method 'checkClassForNull' started");
        if (cls == null) {
            throw new NullPointerException("Class passed as a parameter is null");
        }
        logger.info("Method 'checkClassForNull' finished");
    }

    private void checkClassConstructors() {
        logger.info("Method 'checkClassConstructors' started");
        Constructor<?>[] constructors = cls.getConstructors();
        if (constructors.length == 0) {
            throw new ORMException("Class is not suitable to build abstract repository. " +
                    "There is no any constructors in class " + cls.getSimpleName());
        }
        logger.info("Method 'checkClassConstructors' finished");
    }

    private void checkClassAnnotations() {
        logger.info("Method 'checkClassAnnotations' started");
        if (!cls.isAnnotationPresent(RepositoryTable.class)) {
            throw new ORMException("Class is not suitable to build abstract repository. " +
                    "@RepositoryTable annotation is missing");
        }
        logger.info("Method 'checkClassAnnotations' finished");
    }

    private void checkTableNameValue() {
        logger.info("Method 'checkTableNameValue' started");
        tableName = cls.getAnnotation(RepositoryTable.class).tableName();
        if (tableName.isBlank() || tableName.isEmpty()) {
            throw new ORMException("@RepositoryTable annotation is available but tablename value is empty or blank");
        }
        logger.debug("Table name: {}", tableName);
        logger.info("Method 'checkTableNameValue' finished");
    }

    private void checkTablePresenceInDb() {
        logger.info("Method 'checkTablePresenceInDb' started");
        DbMigrator dbMigrator = new DbMigrator(dataSource);
        if (!dbMigrator.tableExist(tableName.toUpperCase())) {
            throw new ORMException("@RepositoryTable annotation is available but there is no table with table name '"
                    + tableName + "' in database");
        }
        logger.info("Method 'checkTablePresenceInDb' finished");
    }

    private void checkFieldsAnnotations() {
        logger.info("Method 'checkFieldsAnnotations' started");
        Field idField = null;
        for (FieldWrapper<T> wrappedField : wrappedFieldList) {
            switch (wrappedField.getField().getAnnotations().length) {
                case 1: {
                    if (wrappedField.getField().isAnnotationPresent(RepositoryIdField.class)) {
                        if (idField != null) {
                            throw new ORMException("Class '" + cls.getSimpleName() +
                                    "' is not suitable to build abstract repository. " +
                                    "@RepositoryIdField annotation is present more than 1 time");
                        }
                        idField = wrappedField.getField();
                    }
                    break;
                }
                case 0:
                    throw new ORMException("Class  '" + cls.getSimpleName() +
                            "' is not suitable to build abstract repository. " +
                            "@RepositoryField or @RepositoryIdField annotation is missing");
                default:
                    throw new IllegalStateException("Field: '" + wrappedField.getField().getName() +
                            "' has more than one annotation. " +
                            "Number of annotations is " + wrappedField.getField().getAnnotations().length);
            }
        }
        if (idField == null) {
            throw new ORMException("Class  '" + cls.getSimpleName() +
                    "' is not suitable to build abstract repository. " +
                    "There is no annotation @RepositoryIdField present in the class");
        }
        logger.info("Method 'checkFieldsAnnotations' finished");
    }
}
