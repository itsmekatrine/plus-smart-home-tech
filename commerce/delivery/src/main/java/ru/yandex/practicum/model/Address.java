package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "address")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    UUID addressId;

    @Column(name = "country", nullable = false, length = 20)
    String country;

    @Column(name = "city", nullable = false, length = 50)
    String city;

    @Column(name = "street", nullable = false, length = 50)
    String street;

    @Column(name = "house", nullable = false, length = 20)
    String house;

    @Column(name = "flat", length = 20)
    String flat;
}
