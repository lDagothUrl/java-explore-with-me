package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.Event;

import java.util.Optional;

public interface MemoryEvent extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Boolean existsByCategoryId(Long id);

    Optional<Event> findByIdAndInitiatorId(Long eventId, long userId);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

}