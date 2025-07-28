package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long deleteCommentByIdAndAuthorId(Long id, Long authorId);

    long deleteCommentById(Long commentId);

    Page<Comment> findAllByAuthorIdOrderByPublishedOnDesc(Long authorId, Pageable pageable);

    Page<Comment> findAllByEventIdOrderByPublishedOnDesc(Long eventId, Pageable pageable);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long authorId);

    Page<Comment> findAllByAuthorIdAndEventIdOrderByPublishedOnDesc(Long authorId, Long eventId, Pageable pageable);
}