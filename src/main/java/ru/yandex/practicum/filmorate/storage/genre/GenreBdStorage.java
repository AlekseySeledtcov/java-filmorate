package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreBdStorage extends BaseStorage<Genre> implements GenreStorage {

    public GenreBdStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    private static final String GET_ALL_GENRE_QUERY = "SELECT * FROM genres";
    private static final String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id=?";
    private static final String PUT_GENRE_BY_FILM_ID_QUERY = "INSERT INTO genre (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_GENRES_BY_FILM_ID_QUERY = "SELECT * FROM genres " +
            "JOIN genre ON genres.genre_id=genre.genre_id WHERE film_id=?";
    private static final String DELETE_GENRE_BY_FILM_ID_QUERY = "DELETE FROM genre WHERE film_id=?";
    private static final String CONTAINS_GENRE_BY_FILM_ID_QUERY = "SELECT COUNT (*) FROM genre WHERE film_Id=?";

    @Override
    public List<Genre> getAllGenres() {
        log.debug("GenreBdStorage. getAllGenres");
        return findMany(GET_ALL_GENRE_QUERY);
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        log.debug("GenreBdStorage. getGenreById {}", id);
        return findOne(GET_GENRE_BY_ID_QUERY, id);
    }

    @Override
    public boolean containsGenre(long filmId) {
        log.debug("GenreBdStorage. deletecontainsGenre filmid {}", filmId);
        long count = jdbc.queryForObject(CONTAINS_GENRE_BY_FILM_ID_QUERY, long.class, filmId);
        return count > 0;
    }

    @Override
    public List<Genre> getGenresByFilmId(long filmId) {
        return findMany(GET_GENRES_BY_FILM_ID_QUERY, filmId);
    }

    @Override
    public void putGenre(long filmId, int genreId) {
        log.debug("GenreBdStorage. putGenre filmid {} genreId {}", filmId, genreId);
        update(PUT_GENRE_BY_FILM_ID_QUERY, filmId, genreId);
    }

    @Override
    public void deleteGenre(long filmId) {
        log.debug("GenreBdStorage. deleteGenre filmid {}", filmId);
        update(DELETE_GENRE_BY_FILM_ID_QUERY, filmId);
    }
}
