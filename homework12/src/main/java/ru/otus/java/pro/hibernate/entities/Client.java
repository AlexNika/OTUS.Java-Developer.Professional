package ru.otus.java.pro.hibernate.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.engine.spi.Status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE clients SET status = 'DELETED' WHERE id = ?;")
@SQLRestriction("status <> 'DELETED'")
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClientProduct> products;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Client(String name, Status status) {
        this.name = name;
        this.status = status;
        this.products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        ClientProduct clientProduct = new ClientProduct(this, product, Status.MANAGED);
        products.add(clientProduct);
        product.getClients().add(clientProduct);
    }

    public void removeProduct(Product product) {
        for (Iterator<ClientProduct> iterator = products.iterator();
             iterator.hasNext(); ) {
            ClientProduct clientProduct = iterator.next();
            if (clientProduct.getClient().equals(this) && clientProduct.getProduct().equals(product)) {
                iterator.remove();
                clientProduct.getProduct().getClients().remove(clientProduct);
                clientProduct.setClient(null);
                clientProduct.setProduct(null);
            }
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status +
                '}';
    }
}
