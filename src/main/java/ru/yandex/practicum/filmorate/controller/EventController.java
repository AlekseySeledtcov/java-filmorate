package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable long id) {
        log.debug("Контроллер событий. Получение ленты событий пользователя с id {}", id);
        return eventService.getUserFeed(id);
    }
}
