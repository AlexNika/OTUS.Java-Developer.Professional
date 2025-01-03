package ru.otus.java.pro.jpql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client_address")
public class Address {
    @Id
    @Column(name = "client_id")
    private Long id;

    @Column(name = "street", nullable = false)
    private String street;

    @OneToOne
    @MapsId
    @JoinColumn(name = "client_id")
    private Client client;

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                '}';
    }
}
