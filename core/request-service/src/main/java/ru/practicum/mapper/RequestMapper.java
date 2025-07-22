package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.model.Request;

@Mapper
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDto toRequestDto(Request request);
}