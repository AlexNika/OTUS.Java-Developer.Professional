package ru.otus.java.pro.design_patterns.util;

public enum sqlQueries {
    CREATE("""
            CREATE TABLE IF NOT EXISTS coffee (
                id bigserial PRIMARY KEY,
                blendName VARCHAR(64) NOT NULL,
                body VARCHAR(64) NOT NULL,
                country VARCHAR(64) NOT NULL,
                intensifier VARCHAR(64) NOT NULL,
                descriptor VARCHAR(64) NOT NULL,
                variety VARCHAR(64) NOT NULL,
                price DECIMAL NOT NULL);
            """),
    READ("""
            SELECT * FROM coffee;
            """),
    INSERT("""
            INSERT INTO coffee (blendName, body, country, intensifier, descriptor, variety, price)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """),
    UPDATE("""
            UPDATE coffee
            SET price = ? WHERE id = ?;
            """);

    public final String query;

    sqlQueries(String query) {
        this.query = query;
    }
}
