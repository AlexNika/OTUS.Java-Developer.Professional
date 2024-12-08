package ru.otus.java.pro.springboot.part1.homework11.repositories;

import ru.otus.java.pro.springboot.part1.homework11.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Optional<Product> save(Product product);
}
