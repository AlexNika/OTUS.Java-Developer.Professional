package ru.otus.java.pro.springboot.part1.homework11.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.java.pro.springboot.part1.homework11.dtos.ProductDto;
import ru.otus.java.pro.springboot.part1.homework11.entities.Product;
import ru.otus.java.pro.springboot.part1.homework11.services.ProductsServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/products")
public class ProductsController {
    Logger logger = LoggerFactory.getLogger(ProductsController.class);

    private final ProductsServiceImpl productsServiceImpl;
    private static final Function<Product, ProductDto> ENTITY_TO_DTO = i -> new ProductDto(i.getId(), i.getProductName(), i.getPrice());

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllItems() {
        List<ProductDto> productDto = productsServiceImpl.getAllItems().stream().map(ENTITY_TO_DTO).toList();
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        Optional<Product> productOptional = productsServiceImpl.getProductById(id);
        if (productOptional.isEmpty()) {
            logger.info("Product with id {} not found", id);
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                    "Product with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        Product product = productOptional.get();
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createNewItem(@RequestBody ProductDto productDto) {
        Optional<Product> productOptional = productsServiceImpl.createNewItem(productDto);
        if (productOptional.isEmpty()) {
            logger.info("Unable to create product: {}", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    "Unable to create product"),
                    HttpStatus.BAD_REQUEST);
        }
        Product product = productOptional.get();
        return new ResponseEntity<>(new ProductDto(product.getId(), product.getProductName(),
                product.getPrice()), HttpStatus.CREATED);
    }
}
