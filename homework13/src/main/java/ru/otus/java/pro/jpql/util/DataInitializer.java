package ru.otus.java.pro.jpql.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.jpql.entities.Address;
import ru.otus.java.pro.jpql.entities.Client;
import ru.otus.java.pro.jpql.entities.Phone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class DataInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);
    private final EntityManager entityManager;

    public void start() {
        LOGGER.info("Method 'start' started");
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            populateClientAddressPhones("John Doe",
                    "5685 S Semoran Blvd",
                    List.of("(407) 275-3827", "(786) 466-1500"));
            populateClientAddressPhones("Jane Smith",
                    "1108 E Northern Lights Blvd",
                    List.of("(786) 466-1500"));
            populateClientAddressPhones("Michael Johnson",
                    "1108 E Northern Lights Blvd",
                    List.of("(407) 880-2277", "(754) 581-2133", "(754) 581-2133"));
            populateClientAddressPhones("Emily Davis",
                    "286 N Palafox St",
                    List.of("(970) 262-3772"));
            populateClientAddressPhones("David Wilson",
                    "8888 SW 136th St #500",
                    List.of("(914) 969-7488"));

            transaction.commit();
        } catch (PersistenceException | IllegalArgumentException e) {
            transaction.rollback();
            LOGGER.error("The error occurred when filling in data", e);
        }
        LOGGER.info("Method 'start' finished");
    }

    private void populateClientAddressPhones(String clientName, String clientAddress, List<String> phones) {
        Set<Phone> phoneSet = new HashSet<>();
        Client client = new Client();
        client.setName(clientName);
        Address address = new Address();
        address.setStreet(clientAddress);
        address.setClient(client);
        if (phones != null && !phones.isEmpty()) {
            phones.forEach(p -> phoneSet.add(new Phone(p, client)));
        }
        client.setAddress(address);
        client.setPhones(phoneSet);
        entityManager.persist(client);
    }
}
