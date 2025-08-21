package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Qualifier("inMemoryUserStorage")
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
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getUsersList() {
        log.info("Получение списка пользователей из хранилища");
        return users.values().stream().toList();
    }


    public boolean containsUserById(long id) {
        log.info("Проверка наличия пользователя с id {} в хранилище", id);
        return users.containsKey(id);
    }

    @Override
    public boolean containsUserByEmail(String email) {
        log.info("Проверка наличия пользователя с email {}", email);
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<User> getUserFriendsList(long id) {
        log.info("Получение списка друзей пользователя с id {}", id);
        User user = users.get(id);
        if (user == null || user.getFriendsList() == null) {
            return List.of();
        }
        return user.getFriendsList().stream()
                .map(userId -> users.get(userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersCommonFriendList(Long id, Long otherId) {
        log.info("Получение общих друзей пользователей {} и {}", id, otherId);
        User user1 = users.get(id);
        User user2 = users.get(otherId);

        if (user1 == null || user2 == null ||
                user1.getFriendsList() == null || user2.getFriendsList() == null) {
            return List.of();
        }

        return user1.getFriendsList().stream()
                .filter(friend -> user2.getFriendsList().contains(friend))
                .map(userId -> users.get(userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getUsersWithSimilarTastes(long userId) {
        log.info("Получение пользователей с похожими вкусами для пользователя ID: {}", userId);
        // В in-memory реализации возвращаем пустой список
        return List.of();
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
