package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.EventDtoRequest;
import ru.practicum.event.dto.EventDtoResponse;
import ru.practicum.event.dto.EventDtoResponseShort;
import ru.practicum.event.model.Event;
import ru.practicum.location.model.Location;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.dto.UserDtoResponseShort;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public static EventDtoResponse toEventDtoResponse(Integer views, Integer requests, Event event, Integer comments) {
        return EventDtoResponse.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDtoResponse(event.getCategory()))
                .confirmedRequests(requests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserDtoResponseShort(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .publishedOn(event.getPublishedOn())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .comments(comments)
                .build();
    }

    public static EventDtoResponseShort toEventDtoResponseShort(Integer view,
                                                                Integer requests,
                                                                CategoryDtoResponse categoryDto,
                                                                UserDtoResponseShort userDto,
                                                                Event event,
                                                                Integer comments) {
        return EventDtoResponseShort.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(requests)
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(view)
                .comments(comments)
                .build();
    }


    public static Event toEvent(EventDtoRequest request, User user, Category category, Location location) {
        return Event.builder()
                .annotation(request.getAnnotation())
                .category(category)
                .paid(request.getPaid())
                .eventDate(request.getEventDate())
                .initiator(user)
                .description(request.getDescription())
                .participantLimit(request.getParticipantLimit())
                .state(EventState.PENDING)
                .createdOn(LocalDateTime.now())
                .location(location)
                .requestModeration(request.getRequestModeration())
                .title(request.getTitle())
                .build();
    }
}