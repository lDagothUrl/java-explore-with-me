package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.enums.EventState;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDtoGetAdmin {

    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    private String rangeStart;

    private String rangeEnd;

}