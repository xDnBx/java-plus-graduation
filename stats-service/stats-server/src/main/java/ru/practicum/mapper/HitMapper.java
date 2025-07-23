package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.stats.HitRequest;
import ru.practicum.model.Hit;

@Mapper
public interface HitMapper {
    Hit requestToHit(HitRequest hitRequest);
}