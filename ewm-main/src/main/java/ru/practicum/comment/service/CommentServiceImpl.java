package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.repository.MemoryComment;
import ru.practicum.comment.dto.CommentDtoRequest;
import ru.practicum.comment.dto.CommentDtoResponse;
import ru.practicum.comment.dto.CommentDtoResponseLong;
import ru.practicum.comment.dto.CommentDtoUpdateRequest;
import ru.practicum.enums.CommentSort;
import ru.practicum.enums.EventState;
import ru.practicum.enums.MessageUpdateInitiator;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.MemoryEvent;
import ru.practicum.exception.exceptions.AccessDeniedException;
import ru.practicum.exception.exceptions.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.MemoryUser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final MemoryComment memoryComment;
    private final MemoryUser memoryUser;
    private final MemoryEvent memoryEvent;

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = getComment(commentId);
        if (comment.getAnswered() != null) {
            List<Comment> messages = memoryComment.findAllByReplyToIdId(commentId);
            for (Comment cmt : messages) {
                cmt.setReplyToId(null);
            }
            memoryComment.saveAll(messages);
        }

        memoryComment.deleteById(commentId);
        log.info("Comment with ID {} was deleted by admin", commentId);
    }

    @Override
    public CommentDtoResponse updateCommentByAdmin(Long commentId, CommentDtoUpdateRequest request) {
        Comment comment = getComment(commentId);
        comment.setText(request.getText());
        comment.setLastUpdatedOn(LocalDateTime.now());
        comment.setUpdateInitiator(MessageUpdateInitiator.ADMIN);
        Comment result = memoryComment.save(comment);

        log.info("Comment with ID {} was updated by admin", commentId);
        return CommentMapper.toCommentDtoResponse(result);
    }

    @Override
    public List<CommentDtoResponseLong> searchUserCommentsByAdmin(Long userId, int from, int size) {
        existsUser(userId);
        Sort sort = Sort.by(CommentSort.CREATED_ON.getTitle());
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Comment> comments = memoryComment.findAllByAuthorId(userId, pageable).toList();

        log.info("{} messages was founded", comments.size());
        return comments.stream()
                .map(CommentMapper::toCommentDtoResponseLong)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoResponse createCommentByUser(Long userId, Long eventId, CommentDtoRequest request) {
        Event event = getEvent(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new AccessDeniedException("You can leave a comment only for a published event");
        }

        User user = getUser(userId);
        Comment reply = null;
        if (request.getReplyToId() != null) {
            reply = getComment(request.getReplyToId());
            if (reply.getAnswered() == null || !reply.getAnswered()) {
                reply.setAnswered(true);
                reply = memoryComment.save(reply);
            }
        }
        MessageUpdateInitiator initiator = MessageUpdateInitiator.USER;
        Comment result = memoryComment.save(CommentMapper.toNewComment(event, user, reply, request, initiator));

        log.info("Create comment ID {} for event ID {}", result.getId(), result.getEvent().getId());
        return CommentMapper.toCommentDtoResponse(result);
    }

    @Override
    public CommentDtoResponse updateCommentByUser(Long userId, Long commentId, CommentDtoUpdateRequest request) {
        Comment comment = getComment(commentId);
        existsUser(userId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new AccessDeniedException("User with id=" + userId + " is not the author of the comment");
        }
        if (comment.getAnswered()) {
            throw new AccessDeniedException("The comment has already been answered.");
        }

        comment.setText(request.getText());
        comment.setLastUpdatedOn(LocalDateTime.now());
        comment.setUpdateInitiator(MessageUpdateInitiator.USER);
        Comment result = memoryComment.save(comment);

        log.info("Comment with ID {} was updated by user", commentId);
        return CommentMapper.toCommentDtoResponse(result);
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        existsUser(userId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new AccessDeniedException("User with id=" + userId + " is not the author of the comment");
        }
        if (comment.getAnswered()) {
            throw new AccessDeniedException("The comment has already been answered.");
        }

        memoryComment.deleteById(commentId);
        log.info("Comment with ID {} was deleted by user", commentId);
    }

    @Override
    public List<CommentDtoResponse> getEventCommentsByUser(Long userId, Long eventId, int from, int size) {
        existsUser(userId);
        Sort sort = Sort.by(CommentSort.CREATED_ON.getTitle());
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Comment> comments = memoryComment.findAllByEventId(eventId, pageable).toList();

        log.info("{} comments for event ID {} was founded", comments.size(), eventId);
        return comments.stream()
                .map(CommentMapper::toCommentDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Integer> getCommentsCountForEvents(List<Event> events) {
        Map<Long, Integer> result = new HashMap<>();

        for (Event event : events) {
            Integer count = memoryComment.countAllByEventId(event.getId()) == null ?
                    0 : memoryComment.countAllByEventId(event.getId());
            result.put(event.getId(), count);
        }
        return result;
    }


    private void existsUser(Long userId) {
        if (!memoryUser.existsUserById(userId)) {
            throw new NotFoundException("User id=" + userId + " not found");
        }
    }

    private User getUser(Long userId) {
        return memoryUser.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id=" + userId + " not found"));
    }

    private Comment getComment(Long commentId) {
        return memoryComment.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment id=" + commentId + " not found"));
    }

    private Event getEvent(Long eventId) {
        return memoryEvent.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event id=" + eventId + " not found"));
    }
}