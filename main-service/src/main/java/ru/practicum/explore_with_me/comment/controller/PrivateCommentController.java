package ru.practicum.explore_with_me.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.MergeCommentRequest;
import ru.practicum.explore_with_me.comment.service.CommentService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

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
