package ru.practicum.request.service;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.RequestDtoResponse;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<RequestDtoResponse> getRequestsByUser(Long userId);

    RequestDtoResponse createRequestByUser(Long userId, Long eventId);

    RequestDtoResponse cancelRequestByUser(Long userId, Long requestId);

    Map<Long, Integer> getConfirmedRequests(List<Event> events);
}