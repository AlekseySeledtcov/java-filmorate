package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFriendshipException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByFriendIdException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendsList;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendListStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;
    @Qualifier("FriendListDbStorage")
    private final FriendListStorage friendListStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FriendListDbStorage") FriendListStorage friendListStorage) {
        this.userStorage = userStorage;
        this.friendListStorage = friendListStorage;
    }

    public User addUser(User user) {
        if (userStorage.containsUserByEmail(user.getEmail())) {
            throw new DuplicatedDataException("Данный пользователь уже есть");
        }
        return userStorage.addUser(user);
    }

    public User update(User user) {
        if (!userStorage.containsUserById(user.getId())) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе update по id ", user.getId());
        }
        return userStorage.updateUser(user);
    }

    public List<User> getUsersList() {
        return userStorage.getUsersList().stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }

    public List<User> deletingFromFriendList(long id, long friendId) {
        if (!userStorage.containsUserById(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе deletingFromFriendList по id ", id);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new NotFoundUserByFriendIdException("Не найден пользователь в методе deletingFromFriendList по friendId ",
                    friendId);
        }
        if (!friendListStorage.containsFriend(id, friendId)) {
            throw new NotFoundFriendshipException(String.format("Для пользователя с id %d " +
                    "не найден друг с id %d", id, friendId), id, friendId);
        }

        friendListStorage.deleteFriend(id, friendId);
        log.debug("Получение списка друзей у пользователя с id после удаления друга");
        return userStorage.getUserFriendsList(id);
    }

    public List<User> getUserFriendList(long id) {
        if (!userStorage.containsUserById(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе getUserFriendList по id ", id);
        }
        return userStorage.getUserFriendsList(id);
    }

    public List<User> getUsersCommonFriendList(Long id, Long otherId) {
        if (!userStorage.containsUserById(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе getUserCommonFriendList по id ", id);
        }
        if (!userStorage.containsUserById(otherId)) {
            throw new NotFoundUserByFriendIdException("Не найден пользователь в методе getUserCommonFriendList по otherId ",
                    otherId);
        }
        return userStorage.getUsersCommonFriendList(id, otherId);
    }

    // Новый метод для подтверждения дружбы
    public void confirmFriendship(long userId, long friendId) {
        validateUsers(userId, friendId);
        // Теперь дружба сразу подтверждена, поэтому этот метод может быть пустым
        // или просто обновлять статус на CONFIRMED (что уже делается при добавлении)
        friendListStorage.updateFriendshipStatus(userId, friendId, "CONFIRMED");
        log.debug("Дружба между {} и {} подтверждена", userId, friendId);
    }

    // Новый метод для получения ожидающих подтверждения запросов
    public List<User> getPendingFriendRequests(long userId) {
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Пользователь не найден", userId);
        }

        // Получаем ID пользователей, которые отправили запросы текущему пользователю
        List<FriendsList> pendingRequests = friendListStorage.getFriendsWithStatus(userId, "PENDING");
        List<Long> userIds = pendingRequests.stream()
                .map(FriendsList::getUserId)
                .collect(Collectors.toList());

        return userStorage.getUsersByIds(userIds);
    }

    // Вспомогательный метод для валидации пользователей
    private void validateUsers(long userId, long friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Пользователь не найден", userId);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new NotFoundUserByFriendIdException("Пользователь не найден", friendId);
        }
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
    }

    // Обновляем метод добавления в друзья для работы со статусом
    public List<User> addToFriendsList(long userId, long friendId) {
        validateUsers(userId, friendId);

        if (friendListStorage.containsFriend(userId, friendId)) {
            throw new DuplicatedDataException("Пользователь уже в списке друзей");
        }

        log.debug("Сервис. Добавление пользователю с id {} друга с friendId {}", userId, friendId);
        friendListStorage.addFriend(userId, friendId);

        return userStorage.getUserFriendsList(userId);
    }
}

