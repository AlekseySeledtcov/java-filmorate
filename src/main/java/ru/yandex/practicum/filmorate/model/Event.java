package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;

@Data
@Builder
public class Event {
    private Long eventId;
    private Long userId;
    private Long entityId;
    private EventType eventType;
    private Operation operation;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;
}
