package ru.otus.java.pro.springboot.part1.homework11.services;

import ru.otus.java.pro.springboot.part1.homework11.dtos.ProductDto;
import ru.otus.java.pro.springboot.part1.homework11.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> getProductById(Long id);
    Optional<Product> createNewItem(ProductDto productDto);
    List<Product> getAllItems();
}
