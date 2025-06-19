package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByFriendIdException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User update(User user) {
        if (!userStorage.containsUser(user.getId())) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе update по id ", user.getId());
        }
        User oldUser = userStorage.getUser(user.getId());
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            oldUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            oldUser.setBirthday(user.getBirthday());
        }
        userStorage.updateUser(oldUser);
        return oldUser;
    }

    public List<User> getUsersList() {
        return userStorage.getUsersList().stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }

    public User addToFriendsList(long id, long friendId) {
        if (!userStorage.containsUser(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе addToFriendsList по id ", id);
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundUserByFriendIdException("Не найден пользователь в методе addToFriendsList по friendId ",
                    friendId);
        }
        log.info("Добавление пользователю с id {} друга с friendId {}", id, friendId);
        User user = userStorage.getUser(id);
        user.updateUserFriendsList(friendId);
        userStorage.updateUser(user);
        User friendUser = userStorage.getUser(friendId);
        friendUser.updateUserFriendsList(id);
        userStorage.updateUser(friendUser);
        return user;
    }

    public User deletingFromFriendList(long id, long friendId) {
        if (!userStorage.containsUser(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе deletingFromFriendList по id ", id);
        }
        if (!userStorage.containsUser(friendId)) {
            throw new NotFoundUserByFriendIdException("Не найден пользователь в методе deletingFromFriendList по friendId ",
                    friendId);
        }
        userStorage.getUser(id).getFriendsList().remove(friendId);
        userStorage.getUser(friendId).getFriendsList().remove(id);
        return userStorage.getUser(id);
    }

    public List<User> getUserFriendList(long id) {
        if (!userStorage.containsUser(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе getUserFriendList по id ", id);
        }
        return userStorage.getUser(id).getFriendsList().stream()
                .map(userStorage::getUser)
                .toList();
    }

    public List<User> getUserCommonFriendList(Long id, Long otherId) {
        if (!userStorage.containsUser(id)) {
            throw new NotFoundUserByIdException("Не найден пользователь в методе getUserCommonFriendList по id ", id);
        }
        if (!userStorage.containsUser(otherId)) {
            throw new NotFoundUserByFriendIdException("Не найден пользователь в методе deletingFromFriendList по otherId ",
                    otherId);
        }
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);
        return user.getFriendsList().stream()
                .filter(otherUser.getFriendsList()::contains)
                .map(userStorage::getUser)
                .toList();
    }
}
