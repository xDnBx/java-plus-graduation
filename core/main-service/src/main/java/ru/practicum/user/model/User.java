package ru.practicum.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@ToString
@Table(name = "users")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "name", nullable = false)
    String name;
}