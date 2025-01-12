package ru.otus.java.pro.design_patterns.entity;

import java.math.BigDecimal;

public interface CoffeeBuilder {
    CoffeeBuilder id(int id);
    CoffeeBuilder blendName(String blendName);
    CoffeeBuilder body(String body);
    CoffeeBuilder country(String country);
    CoffeeBuilder intensifier(String intensifier);
    CoffeeBuilder descriptor(String descriptor);
    CoffeeBuilder variety(String variety);
    CoffeeBuilder price(BigDecimal price);
}
