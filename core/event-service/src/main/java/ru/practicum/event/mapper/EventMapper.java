package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.dto.event.*;
import ru.practicum.event.model.Event;

@Mapper
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.dto.event.enums.EventState.PENDING)")
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    Event toEvent(NewEventDto newEventDto);

    EventFullDto toFullDto(Event event);

    EventShortDto toShortDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void updateUserRequest(UpdateEventUserRequest userRequest, @MappingTarget Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void patchUserRequest(AdminPatchEventDto adminPatchEventDto, @MappingTarget Event event);
}