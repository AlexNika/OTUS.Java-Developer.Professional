package ru.otus.java.pro.design_patterns.entity;

import java.math.BigDecimal;

public record Coffee(long id,
                     String blendName,
                     String body,
                     String country,
                     String intensifier,
                     String descriptor,
                     String variety,
                     BigDecimal price) {
}