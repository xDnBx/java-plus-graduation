package ru.practicum.explore_with_me.comment.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentResponse;
import ru.practicum.explore_with_me.comment.dto.MergeCommentRequest;
import ru.practicum.explore_with_me.comment.service.CommentService;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminCommentController {
    private final CommentService commentService;

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
