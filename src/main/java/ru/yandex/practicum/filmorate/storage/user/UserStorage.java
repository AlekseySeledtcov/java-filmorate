package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getUser(long id);

    List<User> getUsersList();

    boolean containsUserById(long id);

    boolean containsUserByEmail(String email);

    List<User> getUserFriendsList(long id);

    List<User> getUsersCommonFriendList(Long id, Long otherId);

    List<Long> getUsersWithSimilarTastes(long userId);

    List<User> getUsersByIds(List<Long> userIds);

}
