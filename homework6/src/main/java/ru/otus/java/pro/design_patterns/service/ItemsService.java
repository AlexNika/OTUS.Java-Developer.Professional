package ru.otus.java.pro.design_patterns.service;

import java.sql.SQLException;

@SuppressWarnings("unused")
public interface ItemsService<T> {
    void populateData(int c) throws SQLException;
    void updateDataPrice() throws SQLException;
}
