package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {
}