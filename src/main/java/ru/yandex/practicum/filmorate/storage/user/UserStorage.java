package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User deleteUser(User user);

    User updateUser(User user);

    User getUser(long id);

    List<User> getUsersList();

    boolean containsUser(long id);
}
