package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Set;

@Repository
public interface SimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    List<EventSimilarity> findAllByEventAIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventBIn(Set<Long> eventIds, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventA(Long eventId, PageRequest pageRequest);

    List<EventSimilarity> findAllByEventB(Long eventId, PageRequest pageRequest);
}