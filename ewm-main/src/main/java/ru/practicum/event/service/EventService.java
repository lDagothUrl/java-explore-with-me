package ru.practicum.event.service;

import ru.practicum.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.RequestDtoResponse;
import ru.practicum.request.dto.RequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestStatusUpdateResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventDtoResponse> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                            String rangeStart, String rangeEnd, int from, int size);

    EventDtoResponse updateEventByAdmin(Long eventId, EventDtoUpdateRequest request);

    List<EventDtoResponseShort> getUserEventsByUser(Long userId, Integer from, Integer size);

    EventDtoResponse createEventByUser(Long userId, EventDtoRequest request);

    EventDtoResponse getEventByUser(Long userId, Long eventId);

    EventDtoResponse updateEventByUser(Long userId, Long eventId, EventDtoUserRequest request);

    List<RequestDtoResponse> getEventRequestsByUser(Long userId, Long eventId);

    RequestStatusUpdateResponse updateEventRequestsByUser(Long userId, Long eventId,
                                                          RequestStatusUpdateRequest request);

    List<EventDtoResponseShort> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                                String rangeStart, String rangeEnd, Boolean available,
                                                String sort, int from, int size, HttpServletRequest request);

    EventDtoResponse getEventPublic(Long id, HttpServletRequest request);

    List<EventDtoResponseShort> buildEventDtoResponseShort(List<Event> events);
}