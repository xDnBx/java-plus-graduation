package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.model.Comment;

@Mapper
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", source = "commentRequest.publishedOn")
    Comment toComment(UpdateCommentDto updateCommentDto, Long eventId, Long userId);

    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", source = "commentRequest.publishedOn")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComment(UpdateCommentDto updateCommentDto, Long eventId, @MappingTarget Comment comment);
}