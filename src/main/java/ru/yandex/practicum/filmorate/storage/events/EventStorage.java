package ru.yandex.practicum.filmorate.storage.events;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getAllEventsByUserId(long userId);

    void addEvent(Event event);
}
