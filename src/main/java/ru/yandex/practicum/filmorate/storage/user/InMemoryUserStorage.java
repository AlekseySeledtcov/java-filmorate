package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
    public Optional<User> getUser(long id) {
        log.info("Получение пользователя с id {}", id);
        return Optional.of(users.get(id));
    }

    public List<User> getUsersList() {
        log.info("Получение списка пользователей из хранилища");
        return users.values().stream().toList();
    }


    public boolean containsUserById(long id) {
        log.info("Проверка наличия пользователя с id {} в хранилише", id);
        return users.containsKey(id);
    }

    @Override
    public boolean containsUserByEmail(String email) {
        return false;
    }

    @Override
    public List<User> getUserFriendsList(long id) {
        return users.get(id).getFriendsList().stream()
                .map(userId -> users.get(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersCommonFriendList(Long id, Long otherId) {
        return users.get(id).getFriendsList().stream()
                .filter(friend -> users.get(otherId).getFriendsList().contains(friend))
                .map(key -> users.get(key))
                .collect(Collectors.toList());
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
