package ru.practicum.compilations.mapper;

import org.mapstruct.*;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;
import ru.practicum.event.model.Event;

import java.util.Set;

@Mapper
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", defaultValue = "false")
    Compilation toCompilation(CompilationDto compilationDto, Set<Event> events);

    CompilationResponse toResponse(Compilation compilation);

    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", defaultValue = "false")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequest(CompilationDto updateCompilationRequest, @MappingTarget Compilation compilation, Set<Event> events);
}