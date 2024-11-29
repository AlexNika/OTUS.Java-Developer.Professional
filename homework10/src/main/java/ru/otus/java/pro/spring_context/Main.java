package ru.otus.java.pro.spring_context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.java.pro.spring_context.config.CtxConfig;
import ru.otus.java.pro.spring_context.dtos.Cart;
import ru.otus.java.pro.spring_context.dtos.ProductRepository;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CtxConfig.class)) {
            ProductRepository productRepository = ctx.getBean(ProductRepository.class);
            Cart cartOne = ctx.getBean(Cart.class);
            Cart cartTwo = ctx.getBean(Cart.class);

            logger.info("Product repository contains: {}", productRepository.findAll());
            logger.info("Find by id:{} in repository: {}", 5, productRepository.findById(5));

            cartOne.addProductById(42);
            cartOne.addProductById(1);
            cartOne.addProductById(2);
            cartOne.addProductById(3);
            logger.info("cartOne: {}", cartOne);
            cartOne.deleteProductById(7);
            cartOne.deleteProductById(2);
            logger.info("cartOne: {}", cartOne);

            cartTwo.addProductById(6);
            cartTwo.addProductById(7);
            cartTwo.addProductById(8);
            logger.info("cartTwo: {}", cartTwo);
            cartTwo.deleteProductById(2);
            cartTwo.deleteProductById(7);
            logger.info("cartTwo: {}", cartTwo);
        }
    }
}
