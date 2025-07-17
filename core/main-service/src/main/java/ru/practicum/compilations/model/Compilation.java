package ru.practicum.compilations.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;

import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "compilations")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "pinned", nullable = false)
    Boolean pinned;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
}