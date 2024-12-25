package ru.otus.java.pro.spring.data.jdbc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    private final Long id;
    private final String title;
    private final int price;
}
