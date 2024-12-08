package ru.otus.java.pro.springboot.part1.homework11.services;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.otus.java.pro.springboot.part1.homework11.dtos.ProductDto;
import ru.otus.java.pro.springboot.part1.homework11.entities.Product;
import ru.otus.java.pro.springboot.part1.homework11.repositories.InMemProductsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductService {
    private final InMemProductsRepository inMemProductsRepository;

    @Override
    public Optional<Product> getProductById(Long id) {
        return inMemProductsRepository.findById(id);
    }

    @Override
    public Optional<Product> createNewItem(@NotNull ProductDto productDto) {
        Product product = new Product(null, productDto.productName(), productDto.price());
        return inMemProductsRepository.save(product);
    }

    @Override
    public List<Product> getAllItems() {
        return inMemProductsRepository.findAll();
    }
}
