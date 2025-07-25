package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendListStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

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

    public List<User> addToFriendsList(long userId, long friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе addToFriendsList по id ", userId);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new NotFoundUserByFriendIdException("Не найден пользователь в методе addToFriendsList по friendId ",
                    friendId);
        }
        if (friendListStorage.containsFriend(userId, friendId)) {
            throw new DuplicatedDataException("Пользователь с id " + friendId +
                    " уже я вляется другом пользователя с id " + userId);
        }

        log.debug("Сервис. Добавление пользователю с id {} друга с friendId {}", userId, friendId);
        friendListStorage.addFriend(userId, friendId);

        log.debug("Сервис. Получение списка друзей пользователя с id {}", userId);
        return userStorage.getUserFriendsList(userId);
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
}
