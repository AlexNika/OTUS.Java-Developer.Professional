package ru.otus.java.pro.spring_context.entity;

import java.math.BigDecimal;

public record Product(long id, String name, BigDecimal price) {
}

