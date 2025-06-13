package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        log.info("Добавление пользователя с id {}", user.getId());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление пользователя с id {}", user.getId());
        return users.put(user.getId(), user);
    }

    @Override
    public User deleteUser(User user) {
        log.info("Удаление пользователя с id {}", user.getId());
        return users.remove(user.getId());
    }

    @Override
    public User getUser(long id) {
        log.info("Получение пользователя с id {}", id);
        return users.get(id);
    }

    public List<User> getUsersList() {
        log.info("Получение списка пользователей из хранилища");
        return users.values().stream().toList();
    }


    public boolean containsUser(long id) {
        log.info("Проверка наличия пользователя с id {} в хранилише", id);
        return users.containsKey(id);
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
