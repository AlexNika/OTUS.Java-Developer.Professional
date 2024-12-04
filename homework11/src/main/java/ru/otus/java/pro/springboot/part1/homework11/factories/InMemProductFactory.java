package ru.otus.java.pro.springboot.part1.homework11.factories;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Synchronized;
import org.springframework.stereotype.Component;
import ru.otus.java.pro.springboot.part1.homework11.entities.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@Component
public class InMemProductFactory implements ProductFactory {
    private List<Product> products;

    @Override
    @PostConstruct
    public void populate() {
        products = new ArrayList<>();
        AtomicLong id = new AtomicLong(1);
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setId(id.getAndIncrement());
            product.setProductName("Product " + product.getId());
            product.setPrice(BigDecimal.valueOf(product.getId() * 10));
            products.add(product);
        }
    }

    @Override
    @Synchronized
    public Long getLastId() {
        return products.get(products.size() - 1).getId();
    }
}
