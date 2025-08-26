package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Qualifier("FilmDbStorage")
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private final LikeStorage likeStorage;
    private final MpaService mpaService;

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper,
                         LikeStorage likeStorage,
                         MpaService mpaService) {
        super(jdbc, mapper, Film.class);
        this.likeStorage = likeStorage;
        this.mpaService = mpaService;
    }

    private static final String BASE_QUERY = "SELECT f.id, f.name, f.description, f.releasedate,f.duration," +
            " mr.mpa_id, mr.mpa_name FROM film AS f JOIN mpa_rating AS mr ON f.rating_id=mr.mpa_id ";
    private static final String FIND_ALL_QUERY = BASE_QUERY;
    private static final String CONTAINS_BY_ID_QUERY = "SELECT COUNT(*) FROM film WHERE id = ?";
    private static final String CONTAINS_BY_NAME_QUERY = "SELECT COUNT(*) FROM film WHERE name = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, releaseDate, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = BASE_QUERY + "WHERE id=?";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, " +
            "releaseDate = ?, duration = ?, rating_id = ? WHERE id = ?";

    private static final String GET_POPULAR_FILMS_QUERY =
            "SELECT f.id, f.name, f.description, f.releasedate, f.duration, " +
                    "mr.mpa_id, mr.mpa_name, COUNT(ll.film_id) AS like_count " +
                    "FROM film AS f " +
                    "JOIN mpa_rating AS mr ON f.rating_id = mr.mpa_id " +
                    "LEFT JOIN like_list AS ll ON f.id = ll.film_id " + // LEFT JOIN вместо JOIN
                    "GROUP BY f.id, f.name, f.description, f.releasedate, f.duration, mr.mpa_id, mr.mpa_name " +
                    "ORDER BY like_count DESC " +
                    "LIMIT ?";

    private static final String GET_FILMS_BY_DIRECTOR_ID_SORTED_BY_YEARS = BASE_QUERY +
            "JOIN film_director AS fd ON f.id=fd.film_id " +
            "WHERE director_id=? " +
            "ORDER BY f.releasedate";
    private static final String GET_FILMS_BY_DIRECTOR_ID_SORTED_BY_LIKE = "SELECT f.id, f.name, " +
            "f.description, f.releasedate,f.duration, mr.mpa_id, mr.mpa_name, COUNT(ll.film_id) AS like_count " +
            "FROM film AS f " +
            "JOIN mpa_rating AS mr ON f.rating_id=mr.mpa_id " +
            "LEFT JOIN like_list as ll ON f.id=ll.film_id " +
            "JOIN film_director AS fd ON f.id=fd.film_id " +
            "WHERE director_id=? " +
            "GROUP BY f.id, f.name, f.description, f.releasedate, f.duration, mr.mpa_id, mr.mpa_name " +
            "ORDER BY like_count DESC";
    private static final String DELETE_FILM_QUERY = "DELETE FROM film WHERE id = ?";
    private static final String GET_FILMS_SEARCH_BY_DIRECTOR = "SELECT f.id, f.name, f.description, f.releasedate,f.duration, mr.mpa_id, mr.mpa_name FROM FILM AS f JOIN mpa_rating AS mr ON f.rating_id=mr.mpa_id JOIN film_director AS fd ON fd.film_id=f.id JOIN director AS d ON fd.director_id=d.id WHERE LOWER (d.name) LIKE ?";
    private static final String GET_FILMS_SEARCH_BY_TITLE = "SELECT f.id, f.name, f.description, f.releasedate,f.duration, mr.mpa_id, mr.mpa_name FROM FILM AS f JOIN mpa_rating AS mr ON f.rating_id=mr.mpa_id WHERE LOWER (f.name) LIKE ?";
    private static final String GET_FILMS_LIKED_BY_USER_QUERY =
            "SELECT f.id, f.name, f.description, f.releasedate, f.duration, mr.mpa_id, mr.mpa_name " +
                    "FROM film f " +
                    "JOIN mpa_rating mr ON f.rating_id = mr.mpa_id " +
                    "JOIN like_list ll ON f.id = ll.film_id " +
                    "WHERE ll.user_id = ?";
    private static final String GET_COMMON_FILMS_QUERY =
            "SELECT f.id, f.name, f.description, f.releasedate, f.duration, " +
                    "mr.mpa_id, mr.mpa_name, COUNT(ll.film_id) AS like_count " +
                    "FROM film f " +
                    "JOIN mpa_rating mr ON f.rating_id = mr.mpa_id " +
                    "JOIN like_list ll ON f.id = ll.film_id " +
                    "WHERE f.id IN (" +
                    "    SELECT l1.film_id " +
                    "    FROM like_list l1 " +
                    "    JOIN like_list l2 ON l1.film_id = l2.film_id " +
                    "    WHERE l1.user_id = ? AND l2.user_id = ?" +
                    ") " +
                    "GROUP BY f.id " +
                    "ORDER BY like_count DESC";

    public long getLikeListsByFilmId(long id) {
        return likeStorage.getLikeListsByFilmId(id).size();
    }


    @Override
    public Film addFilm(Film film) {
        log.debug("FilmDbStorage. addFilm добавление фильма с именем {}", film.getName());
        long filmId = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaService.getMpaById(film.getMpa().getId()).getId()
        );
        film.setId(filmId);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("FilmDbStorage. updateFilm. с id {}", film.getId());
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public List<Film> getPopularFilmList(int count) {
        log.debug("FilmDbStorage. getPopularFilmList count {}", count);
        return findMany(GET_POPULAR_FILMS_QUERY, count);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        log.debug("FilmDbStorage. getFilm. с id {}", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public boolean containsFilmById(long id) {
        log.debug("FilmDbStorage. containsFilmById Проверка ниличия фильма в БД с id {}", id);
        Long count = jdbc.queryForObject(CONTAINS_BY_ID_QUERY, Long.class, id);
        return count > 0;

    }

    @Override
    public boolean containsFilmByName(String name) {
        log.debug("FilmDbStorage. containsFilmByName Проверка ниличия фильма в БД с name {}", name);
        Long count = jdbc.queryForObject(CONTAINS_BY_NAME_QUERY, Long.class, name);
        return count > 0;
    }

    @Override
    public List<Film> getFilmsByDirectorSorted(long directorId, String sortedBy) {
        log.debug("FilmDbStorage. getFilmsByDirectorSorted directorId {} sortedBy {}", directorId, sortedBy);
        List<Film> films;
        if (sortedBy.equals("year")) {
            films = findMany(GET_FILMS_BY_DIRECTOR_ID_SORTED_BY_YEARS, directorId);
        } else {
            films = findMany(GET_FILMS_BY_DIRECTOR_ID_SORTED_BY_LIKE, directorId);
        }
        return films;
    }

    public long getLikeListsByFilmId(long id) {
        return likeStorage.getLikeListsByFilmId(id).size();
    }

    private static final String GET_FILMS_LIKED_BY_USER_QUERY =
            "SELECT f.id, f.name, f.description, f.releasedate, f.duration, mr.mpa_id, mr.mpa_name " +
                    "FROM film f " +
                    "JOIN mpa_rating mr ON f.rating_id = mr.mpa_id " +
                    "JOIN like_list ll ON f.id = ll.film_id " +
                    "WHERE ll.user_id = ?";

    private static final String GET_FILMS_NOT_LIKED_BY_USER_QUERY =
            "SELECT f.id, f.name, f.description, f.releasedate, f.duration, mr.mpa_id, mr.mpa_name " +
                    "FROM film f " +
                    "JOIN mpa_rating mr ON f.rating_id = mr.mpa_id " +
                    "WHERE f.id NOT IN (" +
                    "    SELECT film_id FROM like_list WHERE user_id = ?" +
                    ")";

    @Override
    public List<Film> getFilmsLikedByUser(long userId) {
        log.debug("Получение фильмов, которые понравились пользователю ID: {}", userId);
        String query = BASE_QUERY +
                " JOIN like_list ll ON f.id = ll.film_id " +
                " WHERE ll.user_id = ?";
        return findMany(query, userId);
    }

    @Override
    public List<Film> getFilmsSearchByDirector(String query) {
        log.debug("FilmDbStorage. getFilmsSearchByDirector query={}", query);
        return findMany(GET_FILMS_SEARCH_BY_DIRECTOR, query);
    }

    @Override
    public List<Film> getFilmsSearchByTitle(String query) {
        log.debug("FilmDbStorage. getFilmsSearchByTitle query={}", query);
        return findMany(GET_FILMS_SEARCH_BY_TITLE, query);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        log.debug("FilmDbStorage. Получение общих фильмов для userId={} и friendId={}", userId, friendId);
        return findMany(GET_COMMON_FILMS_QUERY, userId, friendId);
    }


    @Override
    public boolean deleteFilm(long id) {
        log.debug("FilmDbStorage. deleteFilm Удаление фильма с id {}", id);
        int rowsDeleted = jdbc.update(DELETE_FILM_QUERY, id);
        return rowsDeleted > 0;
    }
}
