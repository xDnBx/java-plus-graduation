package ru.practicum.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.compilations.model.Compilation;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long>, JpaSpecificationExecutor<Compilation> {
    Optional<Compilation> deleteCompilationById(Long compilationId);
}