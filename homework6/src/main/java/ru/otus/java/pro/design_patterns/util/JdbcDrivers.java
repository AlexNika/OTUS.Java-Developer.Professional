package ru.otus.java.pro.design_patterns.util;

public enum JdbcDrivers {
    H2("org.h2.Driver"),
    MYSQL("com.mysql.jdbc.Driver"),
    POSTGRESQL("org.postgresql.Driver");

    private final String driverName;

    JdbcDrivers(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }
}
