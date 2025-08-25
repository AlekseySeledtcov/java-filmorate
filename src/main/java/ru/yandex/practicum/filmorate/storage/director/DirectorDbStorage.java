package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Qualifier("DirectorDbStorage")
@Repository
public class DirectorDbStorage extends BaseStorage<Director> implements DirectorStorage {
    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper, Director.class);
    }

    private static final String GET_ALL_DIRECTORS_QUERY = "SELECT * FROM director";
    private static final String GET_ALL_DIRECTORS_BY_FILM_ID_QUERY = "SELECT d.id, d.name FROM film_director AS fd JOIN director AS d ON fd.director_id=d.id WHERE film_id=?";
    private static final String GET_DIRECTOR_BY_ID_QUERY = "SELECT * FROM director WHERE ID=?";
    private static final String POST_DIRECTOR_QUERY = "INSERT INTO director (name) VALUES (?)";
    private static final String PUT_DIRECTOR_QUERY = "UPDATE director SET name=? WHERE id=?";
    private static final String DELETE_DIRECTOR_BY_ID_QUERY = "DELETE FROM director WHERE id=?";
    private static final String CONTAINS_DIRECTOR_BY_ID_QUERY = "SELECT COUNT(*) FROM director WHERE id=?";
    private static final String DELETE_DIRECTORS_FROM_FILM_QUERY = "DELETE FROM film_director WHERE film_id=?";
    private static final String PUT_DIRECTORS_TO_FILM = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";


    @Override
    public List<Director> getDirectors() {
        log.debug("DirectorDbStorage. getDirectors");
        return findMany(GET_ALL_DIRECTORS_QUERY);
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        log.debug("DirectorDbStorage. getDirectorById id={}", id);
        return findOne(GET_DIRECTOR_BY_ID_QUERY, id);
    }

    @Override
    public Director postDirector(Director director) {
        log.debug("DirectorDbStorage. postDirector name={}", director.getName());
        long id = insert(POST_DIRECTOR_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director putDirector(Director director) {
        log.debug("DirectorDbStorage. putDirector name={}", director.getName());
        update(PUT_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        log.debug("DirectorDbStorage. deleteDirector id={}", id);
        jdbc.update(DELETE_DIRECTOR_BY_ID_QUERY, id);
    }

    @Override
    public boolean containsDirectorById(long id) {
        long count = jdbc.queryForObject(CONTAINS_DIRECTOR_BY_ID_QUERY, long.class, id);
        return count > 0;
    }

    @Override
    public void deleteDirectorsFromFilm(long id) {
        log.debug("DirectorDbStorage. deleteDirectorsFromFilm id={}", id);
        jdbc.update(DELETE_DIRECTORS_FROM_FILM_QUERY, id);
    }

    @Override
    public void putDirectorsToFilm(long directorId, long filmId) {
        jdbc.update(PUT_DIRECTORS_TO_FILM, filmId, directorId);
    }

    @Override
    public List<Director> getDirectorsByFilmId(long id) {
        log.debug("DirectorDbStorage. getDirectorsByFilmId id={}", id);
        return findMany(GET_ALL_DIRECTORS_BY_FILM_ID_QUERY, id);
    }
}
