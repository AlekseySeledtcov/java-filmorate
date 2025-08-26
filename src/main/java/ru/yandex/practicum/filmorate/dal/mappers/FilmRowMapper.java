package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaService mpaService;

    public FilmRowMapper(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        Timestamp releaseDate = resultSet.getTimestamp("releaseDate");
        film.setReleaseDate(releaseDate.toLocalDateTime().toLocalDate());

        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")));

        // Добавляем количество лайков, если есть в результате
        try {
            film.setLikesCount(resultSet.getLong("like_count"));
        } catch (SQLException e) {
            // Если колонки нет, оставляем значение по умолчанию
            film.setLikesCount(0);
        }

        return film;
    }
}