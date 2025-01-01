package ru.otus.java.pro.hibernate.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SourceType;
import org.hibernate.engine.spi.Status;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE client_product SET status = 'DELETED' WHERE client_id = ? AND product_id = ?;")
@SQLRestriction("status <> 'DELETED'")
@Table(name = "client_product")
public class ClientProduct {

    @EmbeddedId
    private ClientProductId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("clientId")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    private Product product;

    @Column(name = "created_on")
    @CreationTimestamp(source = SourceType.DB)
    private Instant createdOn;

    @Enumerated(EnumType.STRING)
    private Status status;

    public ClientProduct(@NotNull Client client, @NotNull Product product, Status status) {
        this.client = client;
        this.product = product;
        this.status = status;
        this.id = new ClientProductId(client.getId(), product.getId());
    }

    @Override
    public String toString() {
        return "ClientProduct{" +
                "id=" + id +
                ", client=" + client.getName() +
                ", product=" + product.getTitle() +
                ", createdOn=" + createdOn +
                ", status=" + status +
                '}';
    }
}
