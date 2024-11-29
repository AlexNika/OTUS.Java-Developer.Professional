package ru.otus.java.pro.spring_context.dtos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.otus.java.pro.spring_context.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Scope("prototype")
public class Cart {
    private static final Logger logger = LogManager.getLogger(Cart.class);

    private final ProductRepository productRepository;
    private final List<Product> cartContent = new ArrayList<>();

    @Autowired
    public Cart(ProductRepository productRepository) {
        this.productRepository = productRepository;
        logger.debug("Creating new instance of Cart");
    }

    public void addProductById(long id) {
        Optional<Product> product = productRepository.findById(id);
        logger.debug("addProductById: id={}, Product findById={}", id, product);
        if (product.isEmpty()) {
            logger.info("addProductById: ProductRepository doesn't contains product with id={}", id);
        } else {
            cartContent.add(product.get());
        }
    }

    public void deleteProductById(long id) {
        Optional<Product> product = productRepository.findById(id);
        logger.debug("deleteProductById: id={}, Product findById={}", id, product);
        if (product.isEmpty()) {
            logger.info("deleteProductById: ProductRepository doesn't contains product with id={}", id);
        } else {
            if (!cartContent.contains(product.get())) {
                logger.info("deleteProductById: Cart doesn't contains product with id={}", id);
            } else {
                cartContent.removeIf(p -> Objects.equals(p, product.get()));
                logger.info("Product with id:{} deleted from cart", id);
            }
        }

    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartContent=" + cartContent +
                '}';
    }
}
