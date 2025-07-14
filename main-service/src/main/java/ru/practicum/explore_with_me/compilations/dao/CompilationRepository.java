package ru.practicum.explore_with_me.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.compilations.model.Compilation;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long>, JpaSpecificationExecutor<Compilation> {
    Optional<Compilation> deleteCompilationById(Long compilationId);
}
