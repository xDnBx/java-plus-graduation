package ru.practicum.explore_with_me.compilations.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.compilations.dao.CompilationRepository;
import ru.practicum.explore_with_me.compilations.dto.CompilationResponse;
import ru.practicum.explore_with_me.compilations.dto.CompilationDto;
import ru.practicum.explore_with_me.compilations.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilations.model.Compilation;
import ru.practicum.explore_with_me.compilations.service.CompilationService;
import ru.practicum.explore_with_me.compilations.specification.CompilationFindSpecification;
import ru.practicum.explore_with_me.error.model.NotFoundException;
import ru.practicum.explore_with_me.event.dao.EventRepository;
import java.util.Collection;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    public CompilationResponse create(CompilationDto compilationDto) {
        Compilation compilation = compilationMapper.createRequestToCompilation(
                compilationDto,
                eventRepository.findAllByIdIn(compilationDto.getEvents()));
        CompilationResponse response = compilationMapper.compilationToResponse(compilationRepository.save(compilation));
        log.info("Compilation with id={} was created", response.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationResponse getCompilationById(Long compId) {
        Compilation compilation = findCompilationById(compId);
        log.info("Compilation with id={} was found", compilation.getId());
        return compilationMapper.compilationToResponse(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CompilationResponse> getCompilations(Boolean pinned, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Compilation> specification = Specification.where(CompilationFindSpecification.byPinned(pinned));
        Page<Compilation> page = compilationRepository.findAll(specification, pageable);

        log.info("Get compilations with {from, size, pinned}={},{},{}", from, size, pinned);

        return page.getContent().stream().map(compilationMapper::compilationToResponse).toList();
    }

    @Override
    public void deleteById(Long compilationId) {
        if (compilationRepository.deleteCompilationById(compilationId).isPresent()) {
            log.info("Compilation with id={} was deleted", compilationId);
        } else {
            throw new NotFoundException(String.format("Compilation with id=%s, not found", compilationId));
        }
    }

    @Override
    public CompilationResponse update(Long compilationId, CompilationDto updateCompilationRequest) {
        Compilation compilation = findCompilationById(compilationId);
        compilationMapper.compilationUpdateRequest(
                updateCompilationRequest,
                compilation,
        eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        CompilationResponse response = compilationMapper.compilationToResponse(
                compilationRepository.save(compilation));
        log.info("Compilation with id={} was updated", response.getId());
        return response;
    }

    private Compilation findCompilationById(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id=%s, not found", compilationId)));
    }
}
