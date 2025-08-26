package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriendship(@PathVariable("id") long id,
                                  @PathVariable("friendId") long friendId) {
        log.debug("Контроллер. Подтверждение дружбы между {} и {}", id, friendId);
        userService.confirmFriendship(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.debug("Контроллер. Удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.debug("Контроллер. Получение пользователя по id {}", id);
        return userService.getUserById(id);
    }
}
