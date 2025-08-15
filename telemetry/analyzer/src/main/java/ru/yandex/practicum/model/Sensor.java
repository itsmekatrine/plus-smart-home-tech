package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "sensors")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sensor {
    @Id
    private String id;

    @Column(name = "hub_id")
    private String hubId;
}
