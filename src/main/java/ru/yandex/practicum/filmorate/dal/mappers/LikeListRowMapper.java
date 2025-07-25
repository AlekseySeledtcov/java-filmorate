package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikeList;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikeListRowMapper implements RowMapper<LikeList> {
    @Override
    public LikeList mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LikeList likeList = new LikeList();

        likeList.setFilmId(resultSet.getLong("film_id"));
        likeList.setUserId(resultSet.getLong("user_id"));
        return likeList;
    }
}