package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dao.CompilationRepository;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.specification.CompilationFindSpecification;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.event.repository.EventRepository;

import java.util.Collection;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {
    final CompilationRepository compilationRepository;
    final CompilationMapper compilationMapper;
    final EventRepository eventRepository;

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