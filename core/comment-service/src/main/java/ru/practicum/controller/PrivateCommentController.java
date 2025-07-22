package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponse;
import ru.practicum.comment.dto.MergeCommentRequest;
import ru.practicum.service.CommentService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PrivateCommentController {
    final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody MergeCommentRequest request, @PathVariable Long userId) {
        return commentService.createComment(request, userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.deleteCommentByIdAndAuthor(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse updateComment(@Valid @RequestBody MergeCommentRequest request,
                                         @PathVariable Long userId,
                                         @PathVariable Long commentId) {
        return commentService.updateCommentByIdAndAuthorId(commentId, userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentResponse> getAllCommentsByUser(@PathVariable Long userId,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllCommentsByUser(userId, from, size);
    }

    @GetMapping("events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentResponse> getAllCommentsByEvent(@PathVariable Long eventId,
                                                             @PathVariable Long userId,
                                                             @RequestParam(defaultValue = "0") Integer from,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getAllCommentsByUserAndEvent(userId, eventId, from, size);
    }
}