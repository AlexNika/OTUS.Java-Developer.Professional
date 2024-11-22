package ru.otus.java.pro.design_patterns.dao;

import java.math.BigDecimal;
import java.util.List;

public interface ItemsDao<T> {
    void generateData(int q);
    void createTable();
    List<T> readData();
    int insertData(T t);
    int updatePriceData(T t, BigDecimal p);
    List<T> getGeneratedData();
}
