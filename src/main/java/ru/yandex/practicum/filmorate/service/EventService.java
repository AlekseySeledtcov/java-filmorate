package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.events.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public void addEvent(Event event) {
        log.info("Добавление события пользователя с id {} в ленту событий", event.getUserId());
        eventStorage.addEvent(event);
    }

    public List<Event> getUserFeed(Long id) {
        log.info("Запрос на получение списка событий пользователя с id {}", id);
        if (!userStorage.containsUserById(id))
            throw new EntityNotFoundException("Не найден пользователь с id ", id);
        return new ArrayList<>(eventStorage.getAllEventsByUserId(id));
    }
}
