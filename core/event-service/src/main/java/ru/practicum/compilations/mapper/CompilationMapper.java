package ru.practicum.compilations.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.event.model.Event;

import java.util.Set;

@Mapper
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", defaultValue = "false")
    Compilation createRequestToCompilation(CompilationDto compilationDto, Set<Event> events);

    CompilationResponse compilationToResponse(Compilation compilation);

    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", defaultValue = "false")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void compilationUpdateRequest(CompilationDto updateCompilationRequest, @MappingTarget Compilation compilation, Set<Event> events);
}