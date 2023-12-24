package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface MemoryComment extends JpaRepository<Comment, Long> {

    List<Comment> findAllByReplyToIdId(Long replyToId);

    Page<Comment> findAllByAuthorId(Long author, Pageable pageable);

    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);

    Integer countAllByEventId(Long eventId);
}