package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponse;
import ru.practicum.comment.dto.MergeCommentRequest;
import ru.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AdminCommentController {
    final CommentService commentService;

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentById(commentId);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse updateComment(@PathVariable Long commentId,
                                         @Valid @RequestBody MergeCommentRequest mergeCommentRequest) {
        return commentService.updateCommentById(commentId, mergeCommentRequest);
    }
}