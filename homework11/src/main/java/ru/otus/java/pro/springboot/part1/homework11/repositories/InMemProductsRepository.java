package ru.otus.java.pro.springboot.part1.homework11.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import ru.otus.java.pro.springboot.part1.homework11.entities.Product;
import ru.otus.java.pro.springboot.part1.homework11.factories.InMemProductFactory;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemProductsRepository implements ProductRepository {

    private final InMemProductFactory inMemProductFactory;
    private final List<Product> products;

    public InMemProductsRepository(@NotNull InMemProductFactory inMemProductFactory) {
        this.inMemProductFactory = inMemProductFactory;
        this.products = inMemProductFactory.getProducts();
    }

    @Override
    public List<Product> findAll() {
        return products;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Product> save(Product product) {
        if (product == null) {
            return Optional.empty();
        }
        if (product.getId() == null) {
            product.setId(inMemProductFactory.getLastId() + 1L);
        }
        inMemProductFactory.getProducts().add(product);
        return Optional.of(product);
    }
}
