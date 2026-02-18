package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendsList;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendListRowMapper implements RowMapper<FriendsList> {
    @Override
    public FriendsList mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FriendsList friendsList = new FriendsList();

        friendsList.setUserId(resultSet.getInt("user_id"));
        friendsList.setFriendId(resultSet.getInt("friend_id"));
        friendsList.setStatus(resultSet.getString("status"));
        return friendsList;
    }
}