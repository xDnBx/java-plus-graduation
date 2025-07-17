package ru.practicum.mapper;

import dto.HitRequest;
import org.mapstruct.Mapper;
import ru.practicum.model.Hit;

@Mapper
public interface HitMapper {
    Hit requestToHit(HitRequest hitRequest);
}