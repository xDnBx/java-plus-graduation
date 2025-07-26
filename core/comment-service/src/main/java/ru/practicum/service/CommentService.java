package ru.practicum.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentDto createComment(UpdateCommentDto updateCommentDto, Long userId);

    void deleteCommentByIdAndAuthor(Long commentId, Long userId);

    Collection<CommentDto> getAllCommentsByUser(Long userId, Integer from, Integer size);

    void deleteCommentById(Long commentId);

    Collection<CommentDto> getAllCommentsByEvent(Long eventId, Integer from, Integer size);

    CommentDto updateCommentByIdAndAuthorId(Long commentId, Long userId, UpdateCommentDto updateCommentDto);

    CommentDto updateCommentById(Long commentId, UpdateCommentDto updateCommentDto);

    Collection<CommentDto> getAllCommentsByUserAndEvent(Long userId, Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);
}