package ru.otus.java.pro.hibernate.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class ClientProductId implements Serializable {

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "product_id")
    private Long productId;
}
