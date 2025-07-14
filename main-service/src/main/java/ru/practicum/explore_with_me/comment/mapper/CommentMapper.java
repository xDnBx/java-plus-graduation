package ru.practicum.explore_with_me.comment.mapper;

import org.mapstruct.*;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.MergeCommentRequest;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.model.User;

@Mapper
public interface CommentMapper {
    @Mapping(target = "author", source = "user")
    @Mapping(target = "event", source = "event")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", source = "commentRequest.publishedOn")
    Comment requestToComment(MergeCommentRequest commentRequest, Event event, User user);

    CommentResponse commentToResponse(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    @Mapping(target = "publishedOn", source = "commentRequest.publishedOn")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComment(MergeCommentRequest commentRequest, Event event, @MappingTarget Comment comment);
}
