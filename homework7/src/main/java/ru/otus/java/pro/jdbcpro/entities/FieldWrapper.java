package ru.otus.java.pro.jdbcpro.entities;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryField;
import ru.otus.java.pro.jdbcpro.annotations.RepositoryIdField;
import ru.otus.java.pro.jdbcpro.exceptions.ORMException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@Getter
public class FieldWrapper<T> {
    private static final Logger logger = LogManager.getLogger(FieldWrapper.class.getName());

    private final Field field;
    private boolean pkField = false;
    private Method getter;
    private Method setter;
    private String fieldName;
    @Getter(AccessLevel.NONE)
    private Class<T> cls;

    public FieldWrapper(Class<T> cls, Field field) {
        this.cls = cls;
        this.field = field;
        this.wrapField();
    }

    public void wrapField() {
        logger.info("Method 'wrapField' started");
        List<Method> classMethods = List.of(cls.getDeclaredMethods());
        String getterName = (isBoolean(field) ? "is" : "get") + capitalizeFirstLetter(field.getName());
        String setterName = "set" + capitalizeFirstLetter(field.getName());
        getter = classMethods.stream()
                .filter(m -> m.getName().equals(getterName))
                .findFirst()
                .orElseThrow(() -> new ORMException("Class is not suitable to build abstract repository. " +
                        "Can't find getter method for field " + field.getName()));
        logger.debug("Getter method for field: {} is: {}", field.getName(), getter.getName());
        setter = classMethods.stream()
                .filter(m -> m.getName().equals(setterName))
                .findFirst()
                .orElseThrow(() -> new ORMException("Class is not suitable to build abstract repository. " +
                        "Can't find setter method for field " + field.getName()));
        logger.debug("Setter method for field: {} is: {}", field.getName(), setter.getName());
        RepositoryIdField repositoryIdFieldAnnotation = field.getDeclaredAnnotation(RepositoryIdField.class);
        RepositoryField repositoryFieldAnnotation = field.getDeclaredAnnotation(RepositoryField.class);
        if (repositoryIdFieldAnnotation != null) {
            fieldName = repositoryIdFieldAnnotation.fieldName() != null
                    && !repositoryIdFieldAnnotation.fieldName().isBlank()
                    ? repositoryIdFieldAnnotation.fieldName() : field.getName();
            pkField = true;
            logger.debug("FieldName for PK field: {} is: {}", field.getName(), fieldName);
        }
        if (repositoryFieldAnnotation != null) {
            fieldName = repositoryFieldAnnotation.fieldName() != null
                    && !repositoryFieldAnnotation.fieldName().isBlank()
                    ? repositoryFieldAnnotation.fieldName() : field.getName();
            logger.debug("FieldName for field: {} is: {}", field.getName(), fieldName);
        }
        logger.info("Method 'wrapField' finished. Field: {} wrapped", field.getName());
    }

    private boolean isBoolean(@NotNull Field f) {
        return f.getType() == boolean.class || f.getType() == Boolean.class;
    }

    private String capitalizeFirstLetter(String s) {
        if (s == null || s.length() <= 1) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public String toString() {
        return "FieldWrapper{" +
                "field=" + field.getName() +
                ", pkField=" + pkField +
                ", getter=" + getter.getName() +
                ", setter=" + setter.getName() +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
