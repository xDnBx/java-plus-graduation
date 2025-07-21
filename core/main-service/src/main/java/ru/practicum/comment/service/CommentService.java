package ru.practicum.comment.service;

import jakarta.validation.Valid;
import ru.practicum.comment.dto.CommentResponse;
import ru.practicum.comment.dto.MergeCommentRequest;

import java.util.Collection;

public interface CommentService {
    CommentResponse createComment(MergeCommentRequest mergeCommentRequest, Long userId);

    void deleteCommentByIdAndAuthor(Long commentId, Long userId);

    Collection<CommentResponse> getAllCommentsByUser(Long userId, Integer from, Integer size);

    void deleteCommentById(Long commentId);

    Collection<CommentResponse> getAllCommentsByEvent(Long eventId, Integer from, Integer size);

    CommentResponse updateCommentByIdAndAuthorId(Long commentId, Long userId, MergeCommentRequest request);

    CommentResponse updateCommentById(Long commentId, @Valid MergeCommentRequest mergeCommentRequest);

    Collection<CommentResponse> getAllCommentsByUserAndEvent(Long userId, Long eventId, Integer from, Integer size);

    CommentResponse getCommentById(Long commentId);
}