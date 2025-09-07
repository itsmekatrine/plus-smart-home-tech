package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dimension {
    @Column(name = "width", nullable = false)
    Double width;

    @Column(name = "height", nullable = false)
    Double height;

    @Column(name = "depth", nullable = false)
    Double depth;
}