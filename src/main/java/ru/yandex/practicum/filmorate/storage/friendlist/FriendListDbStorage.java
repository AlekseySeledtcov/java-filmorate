package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendsList;

import java.util.List;

@Slf4j
@Qualifier("FriendListDbStorage")
@Repository
public class FriendListDbStorage extends BaseStorage<FriendsList> implements FriendListStorage {

    public FriendListDbStorage(JdbcTemplate jdbc, RowMapper<FriendsList> mapper) {
        super(jdbc, mapper, FriendsList.class);
    }

    private static final String INSERT_QUERY = "INSERT INTO friends_list(user_id, friend_id, status) " +
            "VALUES (?, ?, 'CONFIRMED')";
    private static final String DELETE_QUERY = "DELETE FROM friends_list WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_FRIEND_USER_QUERY = "SELECT * FROM friends_list WHERE user_id = ? " +
            "AND friend_id = ?";
    private static final String UPDATE_STATUS_QUERY = "UPDATE friends_list SET status = ? WHERE user_id = ? " +
            "AND friend_id = ?";
    private static final String GET_FRIENDS_WITH_STATUS_QUERY = "SELECT * FROM friends_list WHERE user_id = ? " +
            "AND status = ?";
    private static final String CONFIRM_ALL_FRIENDSHIPS_QUERY =
            "UPDATE friends_list SET status = 'CONFIRMED' WHERE status = 'PENDING'";


    @Override
    public void addFriend(long userId, long friendId) {
        log.debug("Хранилище. Добавление пользователю с id {} друга с friendId {}", userId, friendId);
        update(INSERT_QUERY, userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        log.debug("Хранилище. Удаление у пользователя с id {} друга с friendId {}", userId, friendId);
        update(DELETE_QUERY, userId, friendId);
    }

    @Override
    public boolean containsFriend(long userId, long friendId) {
        return findOne(SELECT_FRIEND_USER_QUERY, userId, friendId).isPresent();
    }

    @Override
    public void updateFriendshipStatus(long userId, long friendId, String status) {
        log.debug("Хранилище. Обновление статуса дружбы между {} и {} на {}", userId, friendId, status);
        update(UPDATE_STATUS_QUERY, status, userId, friendId);
    }

    @Override
    public List<FriendsList> getFriendsWithStatus(long userId, String status) {
        log.debug("Хранилище. Получение друзей пользователя {} со статусом {}", userId, status);
        return findMany(GET_FRIENDS_WITH_STATUS_QUERY, userId, status);
    }
}