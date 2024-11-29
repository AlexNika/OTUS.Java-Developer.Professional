package ru.otus.java.pro.spring_context.dtos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.otus.java.pro.spring_context.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    private static final Logger logger = LogManager.getLogger(ProductRepository.class);

    private final List<Product> productList = new ArrayList<>();

    public ProductRepository() {
        for (long i = 1; i < 11; i++) {
            this.productList.add(new Product(i, "Product " + i, BigDecimal.valueOf(i * 10)));
        }
        logger.debug("Creating instance of ProductRepository");
    }

    public List<Product> findAll() {
        return productList;
    }

    public Optional<Product> findById(long id) {
        return productList.stream().filter(product -> product.id() == id).findFirst();
    }
}
