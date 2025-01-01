package ru.otus.java.pro.hibernate;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import ru.otus.java.pro.hibernate.daos.ClientDao;
import ru.otus.java.pro.hibernate.daos.ProductDao;
import ru.otus.java.pro.hibernate.entities.Client;
import ru.otus.java.pro.hibernate.entities.ClientProduct;
import ru.otus.java.pro.hibernate.entities.Product;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import static java.lang.System.out;

@AllArgsConstructor
public class Console {
    private static final Logger logger = LogManager.getLogger(Console.class.getName());

    private ClientDao clientDao;
    private ProductDao productDao;

    public void start() {
        this.displayMenu();
        boolean consoleActive = true;
        try (Scanner scanner = new Scanner(System.in)) {
            while (consoleActive) {
                out.print("Please select the menu item: ");
                consoleActive = isConsoleActive(scanner);
                out.println();
            }
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            logger.error("The error occurs when trying to read scanner input: {}", e.getMessage());
        }
    }

    private boolean isConsoleActive(@NotNull Scanner scanner) {
        List<Product> products;
        List<ClientProduct> clientProductList;
        List<Client> clients;
        Product product;
        Client client;
        String inputClientId = "Input client id: ";
        String inputProductId = "Input product id: ";
        switch (scanner.nextInt()) {
            case 1 -> {
                clients = clientDao.findAll();
                clients.forEach(out::println);
            }
            case 2 -> {
                products = productDao.findAll();
                products.forEach(out::println);
            }
            case 3 -> {
                out.print(inputClientId);
                out.println(clientDao.findById((long) scanner.nextInt()));
            }
            case 4 -> {
                out.print(inputProductId);
                out.println(productDao.findById((long) scanner.nextInt()));
            }
            case 5 -> {
                out.print("Input product name: ");
                Optional<Product> productOptional = productDao.findProductByTitle(scanner.next());
                productOptional.ifPresent(out::println);
            }
            case 6 -> {
                out.print(inputClientId);
                clientProductList = clientDao.findProductsByClientId((long) scanner.nextInt());
                clientProductList.forEach(p -> out.println(p.getProduct()));
            }
            case 7 -> {
                out.print(inputProductId);
                clientProductList = productDao.findClientsByProductId((long) scanner.nextInt());
                clientProductList.forEach(c -> out.println(c.getClient()));
            }
            case 8 -> {
                out.print(inputClientId);
                client = clientDao.findById((long) scanner.nextInt());
                if (client != null) clientDao.deleteObject(client);
            }
            case 9 -> {
                clients = clientDao.findAll();
                clients.forEach(c -> clientDao.deleteObject(c));
                out.println("All clients were deleted");
            }
            case 10 -> {
                out.print(inputProductId);
                product = productDao.findById((long) scanner.nextInt());
                if (product != null) productDao.deleteObject(product);
            }
            case 11 -> {
                products = productDao.findAll();
                products.forEach(p -> productDao.deleteObject(p));
                out.println("All products were deleted");
            }
            case 0 -> {
                out.println("Goodbye!");
                return false;
            }
            default -> out.println("Unknown input, try again!");
        }
        return true;
    }

    private void displayMenu() {
        out.println("1. Display all clients");
        out.println("2. Display all products");
        out.println("3. Find client by id");
        out.println("4. Find product by id");
        out.println("5. Find product by name");
        out.println("6. Find all products that the client has bought");
        out.println("7. Find all clients who bought the product");
        out.println("8. Delete client by id");
        out.println("9. Delete all clients");
        out.println("10. Delete product by id");
        out.println("11. Delete all products");
        out.println("0. Exit");
        out.println();
    }
}