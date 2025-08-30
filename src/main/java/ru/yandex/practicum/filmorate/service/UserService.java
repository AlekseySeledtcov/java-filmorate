package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.friendlist.FriendListStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendListStorage friendListStorage;
    private final LikeStorage likeStorage;
    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public UserService(UserStorage userStorage,
                       FriendListStorage friendListStorage,
                       LikeStorage likeStorage,
                       ReviewStorage reviewStorage,
                       EventService eventService) {
        this.userStorage = userStorage;
        this.friendListStorage = friendListStorage;
        this.likeStorage = likeStorage;
        this.reviewStorage = reviewStorage;
        this.eventService = eventService;
    }

    public User addUser(User user) {
        userValidator(user);
        if (userStorage.containsUserByEmail(user.getEmail())) {
            throw new DuplicatedDataException("Данный пользователь уже есть");
        }
        return userStorage.addUser(user);
    }

    public User update(User user) {
        if (!userStorage.containsUserById(user.getId())) {
            throw new EntityNotFoundException("Не найден пользователь в методе update по id ", user.getId());
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
            throw new EntityNotFoundException("Не найден пользователь в методе deletingFromFriendList по id ", id);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new EntityNotFoundException("Не найден пользователь в методе deletingFromFriendList по friendId ",
                    friendId);
        }
        if (!friendListStorage.containsFriend(id, friendId)) {
            throw new EntityNotFoundException(String.format("Для пользователя с id %d " +
                    "не найден друг", id), id);
        }

        friendListStorage.deleteFriend(id, friendId);
        Event event = Event.builder()
                .userId(id)
                .entityId(friendId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .build();

        log.debug("Добавление события удаления друга: {}", event);
        eventService.addEvent(event);
        log.debug("Получение списка друзей у пользователя с id после удаления друга");
        return userStorage.getUserFriendsList(id);
    }

    public List<User> getUserFriendList(long id) {
        if (!userStorage.containsUserById(id)) {
            throw new EntityNotFoundException("Не найден пользователь в методе getUserFriendList по id ", id);
        }
        return userStorage.getUserFriendsList(id);
    }

    public List<User> getUsersCommonFriendList(Long id, Long otherId) {
        if (!userStorage.containsUserById(id)) {
            throw new EntityNotFoundException("Не найден пользователь в методе getUserCommonFriendList по id ", id);
        }
        if (!userStorage.containsUserById(otherId)) {
            throw new EntityNotFoundException("Не найден пользователь в методе getUserCommonFriendList по otherId ",
                    otherId);
        }
        return userStorage.getUsersCommonFriendList(id, otherId);
    }

    public void confirmFriendship(long userId, long friendId) {
        validateUsers(userId, friendId);

        friendListStorage.updateFriendshipStatus(userId, friendId, "CONFIRMED");
        log.debug("Дружба между {} и {} подтверждена", userId, friendId);
    }

    private void validateUsers(long userId, long friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден", userId);
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new EntityNotFoundException("Пользователь не найден", friendId);
        }
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
    }

    public List<User> addToFriendsList(long userId, long friendId) {
        validateUsers(userId, friendId);

        if (friendListStorage.containsFriend(userId, friendId)) {
            throw new DuplicatedDataException("Пользователь уже в списке друзей");
        }

        log.debug("Добавление пользователю с id {} друга с friendId {}", userId, friendId);
        friendListStorage.addFriend(userId, friendId);
        Event event = Event.builder()
                .userId(userId)
                .entityId(friendId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .build();
        log.debug("Добавление события в ленту: {}", event);
        eventService.addEvent(event);
        return userStorage.getUserFriendsList(userId);
    }


    public void deleteUser(long id) {
        log.debug("deleteUser Удаление пользователя с id {}", id);

        if (!userStorage.containsUserById(id)) {
            throw new EntityNotFoundException("Не найден пользователь для удаления по id ", id);
        }

        friendListStorage.deleteAllFriendsForUser(id);
        likeStorage.deleteAllLikesForUser(id);
        reviewStorage.deleteReviewsByUserId(id);
        reviewStorage.deleteReviewRatingsByUserId(id);

        boolean wasDeleted = userStorage.deleteUser(id);
        if (!wasDeleted) {
            throw new EntityNotFoundException("Не удалось удалить пользователя по id ", id);
        }
        log.debug("Пользователь с id {} успешно удален: {}", id, wasDeleted);
    }

    public User getUserById(long id) {
        if (!userStorage.containsUserById(id)) {
            throw new EntityNotFoundException("Не найден пользователь по id ", id);
        }
        return userStorage.getUser(id).orElseThrow(() ->
                new EntityNotFoundException("Не найден пользователь по id ", id));
    }

    private User userValidator(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        return user;
    }

}
