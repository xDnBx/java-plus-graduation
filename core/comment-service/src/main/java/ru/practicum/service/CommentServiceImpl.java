package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PublicationException;
import ru.practicum.feign.EventClient;
import ru.practicum.feign.UserClient;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

import java.util.Collection;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {
    final CommentRepository commentRepository;
    final CommentMapper commentMapper;
    final UserClient userClient;
    final EventClient eventClient;

    @Override
    public CommentDto createComment(UpdateCommentDto updateCommentDto, Long userId) {
        userClient.getUserById(userId);
        EventFullDto event = findEventById(updateCommentDto.getEventId());

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new PublicationException("Event must be published");
        }

        Comment comment = commentMapper.toComment(updateCommentDto, event.getId(), userId);
        CommentDto response = commentMapper.toDto(commentRepository.save(comment));
        log.info("Comment id = {} was created by user id = {}", response.getId(), response.getAuthor().getId());
        return response;
    }

    @Override
    public void deleteCommentByIdAndAuthor(Long commentId, Long userId) {
        if (commentRepository.deleteCommentByIdAndAuthor_Id(commentId, userId) != 0) {
            log.info("Comment with id = {} was deleted by user id = {}", commentId, userId);
        } else {
            throw new NotFoundException(String.format("Comment with id = %d by author id = %d was not found", commentId, userId));
        }
    }

    @Override
    public void deleteCommentById(Long commentId) {
        if (commentRepository.deleteCommentById(commentId) != 0) {
            log.info("Comment with id={} was deleted", commentId);
        } else {
            throw new NotFoundException(String.format("Comment with id = %d was not found", commentId));
        }
    }

    @Override
    public CommentDto updateCommentByIdAndAuthorId(Long commentId, Long userId, UpdateCommentDto updateCommentDto) {
        Comment oldComment = commentRepository.findByIdAndAuthor_Id(commentId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id=%d by author id = %d was not found", commentId, userId)));

        if (!oldComment.getEventId().equals(updateCommentDto.getEventId())) {
            throw new DataIntegrityViolationException("Event Id not correct");
        }

        commentMapper.updateComment(
                updateCommentDto,
                findEventById(updateCommentDto.getEventId()).getId(),
                oldComment);

        CommentDto response = commentMapper.toDto(commentRepository.save(oldComment));
        log.info("Comment id = {} was updated by user id = {}", response.getId(), response.getAuthor().getId());
        return response;
    }

    @Override
    public CommentDto updateCommentById(Long commentId, UpdateCommentDto updateCommentDto) {
        Comment oldComment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id = %d was not found", commentId)));

        if (!oldComment.getEventId().equals(updateCommentDto.getEventId())) {
            throw new DataIntegrityViolationException("Event id not correct");
        }

        commentMapper.updateComment(
                updateCommentDto,
                findEventById(updateCommentDto.getEventId()).getId(),
                oldComment);

        CommentDto response = commentMapper.toDto(commentRepository.save(oldComment));
        log.info("Comment id = {} was updated", response.getId());
        return response;
    }

    @Override
    public Collection<CommentDto> getAllCommentsByUser(Long userId, Integer from, Integer size) {
        log.info("Get all comments for user id = {}", userId);
        return commentRepository.findAllByAuthor_IdOrderByPublishedOnDesc(userId, createPageable(from, size))
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public Collection<CommentDto> getAllCommentsByEvent(Long eventId, Integer from, Integer size) {
        log.info("Get all comments for event id = {}", eventId);
        return commentRepository.findAllByEvent_IdOrderByPublishedOnDesc(eventId, createPageable(from, size))
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public Collection<CommentDto> getAllCommentsByUserAndEvent(Long userId, Long eventId, Integer from, Integer size) {
        log.info("Get all comments for event id = {} and user id = {}", eventId, userId);
        return commentRepository.findAllByAuthor_IdAndEvent_IdOrderByPublishedOnDesc(userId, eventId, createPageable(from, size))
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("Get comment with id = {}", commentId);
        return commentMapper.toDto(commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Comment with id = %d was not found", commentId))));
    }

    private Pageable createPageable(Integer from, Integer size) {
        int pageNumber = from / size;
        return PageRequest.of(pageNumber, size);
    }

    private EventFullDto findEventById(Long eventId) {
        return eventClient.getEventByIdFeign(eventId);
    }
}