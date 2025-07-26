package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue
    @Column(name = "id")
    Long id;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    String text;

    @Column(name = "published_on", nullable = false)
    LocalDateTime publishedOn;

    @ToString.Exclude
    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Long authorId;

    @ToString.Exclude
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Long eventId;
}