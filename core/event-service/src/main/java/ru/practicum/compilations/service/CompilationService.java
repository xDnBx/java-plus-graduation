package ru.practicum.compilations.service;

import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;

import java.util.Collection;

public interface CompilationService {
    CompilationResponse create(CompilationDto compilationDto);

    void deleteById(Long compilationId);

    CompilationResponse getCompilationById(Long compId);

    Collection<CompilationResponse> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationResponse update(Long compilationId, CompilationDto updateCompilationRequest);
}