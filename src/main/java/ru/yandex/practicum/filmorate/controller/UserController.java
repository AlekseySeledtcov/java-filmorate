package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsersList();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriendsList(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        return userService.addToFriendsList(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deletingFromFriendList(@PathVariable long id, @PathVariable long friendId) {
        return userService.deletingFromFriendList(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriendList(@PathVariable long id) {
        return userService.getUserFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUserCommonFriendList(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getUserCommonFriendList(id, otherId);
    }
}
