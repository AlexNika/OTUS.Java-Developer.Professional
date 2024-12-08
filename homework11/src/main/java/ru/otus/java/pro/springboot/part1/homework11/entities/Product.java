package ru.otus.java.pro.springboot.part1.homework11.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String productName;
    private BigDecimal price;
}
