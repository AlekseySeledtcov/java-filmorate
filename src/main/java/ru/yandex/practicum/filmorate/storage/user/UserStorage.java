package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    User addUser(User user);

    User deleteUser(User user);

    User updateUser(User user);

    User getUser(long id);

    List<User> getUsersList();

    boolean containsUser(long id);
}
