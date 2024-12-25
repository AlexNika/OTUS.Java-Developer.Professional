package ru.otus.java.pro.spring.data.jdbc.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.java.pro.spring.data.jdbc.dtos.ProductDto;
import ru.otus.java.pro.spring.data.jdbc.entities.Product;
import ru.otus.java.pro.spring.data.jdbc.repositories.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;

    private static final Function<Product, ProductDto> ENTITY_TO_DTO = p ->
            ProductDto.builder().id(p.getId()).title(p.getTitle()).price(p.getPrice()).build();

    private static final Function<ProductDto, Product> DTO_TO_ENTITY = d ->
            Product.builder().id(d.getId()).title(d.getTitle()).price(d.getPrice()).build();


    @GetMapping
    public List<ProductDto> getAll() {
        return productRepository.findAll().stream()
                .map(ENTITY_TO_DTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDto getOne(@PathVariable Long id) {
        Optional<ProductDto> productDtoOptional = productRepository.findById(id).map(ENTITY_TO_DTO);
        return productDtoOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @GetMapping("/by-ids")
    public List<ProductDto> getMany(@RequestParam List<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .map(ENTITY_TO_DTO)
                .toList();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public Product delete(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
        }
        return product;
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        productRepository.deleteAllById(ids);
    }

    @DeleteMapping
    public void deleteAll() {
        productRepository.deleteAll();
    }
}
