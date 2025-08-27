package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("Контроллер. Добавление нового пользователя");
        return userService.addUser(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.debug("Контроллер. Обновление пользователя с id {} ", user.getId());
        return userService.update(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Контроллер. Получение списка пользователей");
        return userService.getUsersList();
    }

    @PutMapping("/{id}/friends/{friend_id}")
    public List<User> addToFriendsList(@PathVariable("id") long id, @PathVariable("friend_id") long friendId) {
        log.debug("Контроллер. Добавление пользователю с id {} друга с friendId {}", id, friendId);
        return userService.addToFriendsList(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friend_id}")
    public List<User> deletingFromFriendList(@PathVariable("id") long id, @PathVariable("friend_id") long friendId) {
        log.debug("Контроллер удаление у пользователя id {} друга с friendId {}", id, friendId);
        return userService.deletingFromFriendList(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriendList(@PathVariable long id) {
        log.debug("Контроллер. Получение списка друзей пользователя с id {} ", id);
        return userService.getUserFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUsersCommonFriendList(@PathVariable Long id, @PathVariable Long otherId) {
        log.debug("Контроллер. Получение списка общих друзей пользователя с id {} " +
                "и другого пользователя с otherId {}", id, otherId);
        return userService.getUsersCommonFriendList(id, otherId);
    }

    // Новый эндпоинт для подтверждения дружбы
    @PutMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriendship(@PathVariable("id") long id,
                                  @PathVariable("friendId") long friendId) {
        log.debug("Контроллер. Подтверждение дружбы между {} и {}", id, friendId);
        userService.confirmFriendship(id, friendId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable long id) {
        log.debug("Контроллер событий. Получение ленты событий пользователя с id {}", id);
        return eventService.getUserFeed(id);
    }
}
