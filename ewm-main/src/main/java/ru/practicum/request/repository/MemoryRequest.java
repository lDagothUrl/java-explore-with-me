package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestEvent;

import java.util.List;

public interface MemoryRequest extends JpaRepository<Request, Long> {

    @Query("select new ru.practicum.request.model.RequestEvent(r.event.id, count(r.id)) from Request as r " +
            "where r.event.id in :ids and r.status = 'CONFIRMED' " +
            "group by r.event.id")
    List<RequestEvent> getConfirmedRequests(List<Long> ids);

    List<Request> findAllByRequesterId(Long userId);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByEventId(Long eventId);

}