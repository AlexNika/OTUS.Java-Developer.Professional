package ru.otus.java.pro.hibernate.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.engine.spi.Status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE products SET status = 'DELETED' WHERE id = ?;")
@SQLRestriction("status <> 'DELETED'")
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(nullable = false)
    private String title;

    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClientProduct> clients;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Product(String title, BigDecimal price, Status status) {
        this.title = title;
        this.price = price;
        this.status = status;
        this.clients = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + title + '\'' +
                ", price=" + price +
                ", status='" + status +
                '}';
    }
}
