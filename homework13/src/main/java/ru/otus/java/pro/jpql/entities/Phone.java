package ru.otus.java.pro.jpql.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "client_phone_numbers")
public class Phone {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Phone(String phoneNumber, Client client) {
        this.phoneNumber = phoneNumber;
        this.client = client;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
