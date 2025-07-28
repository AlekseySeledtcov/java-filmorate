package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendsList;

@Slf4j
@Qualifier("FriendListDbStorage")
@Repository
public class FriendListDbStorage extends BaseStorage<FriendsList> implements FriendListStorage {

    public FriendListDbStorage(JdbcTemplate jdbc, RowMapper<FriendsList> mapper) {
        super(jdbc, mapper, FriendsList.class);
    }

    private static final String INSERT_QUERY = "INSERT INTO friends_list(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM friends_List WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_FRIEND_USER_QUERY = "SELECT * FROM friends_list " +
            "WHERE user_id = ? AND friend_id = ?";

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
}
