package ru.practicum.explore_with_me.comment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "comments")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
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
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    User author;

    @ToString.Exclude
    @JoinColumn(name = "event_id")
    @ManyToOne(fetch = FetchType.LAZY)
    Event event;
}
