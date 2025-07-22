package ru.practicum.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long deleteCommentByIdAndAuthor_Id(Long id, Long authorId);

    long deleteCommentById(Long commentId);

    Page<Comment> findAllByAuthor_IdOrderByPublishedOnDesc(Long userId, Pageable pageable);

    Page<Comment> findAllByEvent_IdOrderByPublishedOnDesc(Long eventId, Pageable pageable);

    Optional<Comment> findByIdAndAuthor_Id(Long commentId, Long authorId);

    Page<Comment> findAllByAuthor_IdAndEvent_IdOrderByPublishedOnDesc(Long userId, Long eventId, Pageable pageable);
}