package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EventSimilarity;

@Repository
public interface SimilarityRepository extends JpaRepository<EventSimilarity, Long> {
}