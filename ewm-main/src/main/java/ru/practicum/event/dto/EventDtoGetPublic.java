package ru.practicum.event.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDtoGetPublic {

    private String text;

    private List<Long> categories;

    private Boolean paid;

    private String rangeStart;

    private String rangeEnd;

    private Boolean available;

    private String sort;

}