package ru.practicum.expore_with_me.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "hits")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "app", nullable = false)
    String app;

    @Column(name = "ip", nullable = false, length = 15)
    String ip;

    @Column(name = "uri", nullable = false)
    String uri;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime timestamp;
}
