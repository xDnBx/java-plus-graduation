package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.enums.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "created", nullable = false)
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    User requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RequestStatus status;
}