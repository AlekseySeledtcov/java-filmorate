package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    UserValidator uv = new UserValidator();

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            user = uv.validate(user);
        } catch (ValidationException exception) {
            log.warn("Ошибка валидации ", exception);
            throw new ValidationException("Ошибка валидации");
        }

        user.setId(getNextId());
        log.info("Создание пользователя с именем {} id {}", user.getName(), user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Не указан ID пользователя");
            throw new ValidationException("Не указан ID пользователя");
        }
        try {
            user = uv.validate(user);
        } catch (ValidationException exception) {
            log.warn("ошибка валидации ", exception);
            throw new ValidationException("Ошибка валидации");
        }

        User oldUser = users.get(user.getId());
        log.info("Обновление пользователя с именем {} id {}", oldUser.getName(), user.getId());
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            oldUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if ((user.getBirthday() != null)) {
            oldUser.setBirthday(user.getBirthday());
        }

        return oldUser;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
