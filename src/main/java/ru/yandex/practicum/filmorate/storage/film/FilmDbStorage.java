package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.sql.Date;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Qualifier("FilmDbStorage")
@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private final LikeStorage likeStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper,
                         LikeStorage likeStorage,
                         MpaService mpaService,
                         GenreService genreService) {
        super(jdbc, mapper, Film.class);
        this.likeStorage = likeStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    private static final String BASE_QUERY = "SELECT f.id, f.name, f.description, f.releasedate,f.duration, mr.mpa_id, mr.mpa_name " +
            "FROM film AS f " +
            "JOIN mpa_rating AS mr ON f.rating_id=mr.mpa_id";
    private static final String FIND_ALL_QUERY = BASE_QUERY;
    private static final String CONTAINS_BY_ID_QUERY = "SELECT COUNT(*) FROM film WHERE id = ?";
    private static final String CONTAINS_BY_NAME_QUERY = "SELECT COUNT(*) FROM film WHERE name = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, releaseDate, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = BASE_QUERY + " WHERE id=?";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, " +
            "releaseDate = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String GET_POPULAR_FILMS_QUERY = "SELECT f.id, f.name, f.description, f.releasedate,f.duration, mr.mpa_id, mr.mpa_name, COUNT (ll.film_id) AS like_count " +
            "FROM film AS f " +
            "JOIN mpa_rating AS mr ON f.rating_id=mr.mpa_id " +
            "JOIN like_list AS ll ON f.id=ll.film_id " +
            "GROUP BY ll.film_id LIMIT ?";

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

        if (film.getGenres().size() != 0) {
            genreService.putGenre(film.getGenres(), film.getId());
        }
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

        if (film.getGenres().size() != 0) {
            genreService.deleteGenre(film.getId());
            genreService.putGenre(film.getGenres(), film.getId());
        }
        return film;
    }

    @Override
    public List<Film> getPopularFilmList(int count) {
        log.debug("FilmDbStorage. getPopularFilmList count {}", count);
        List<Film> films = findMany(GET_POPULAR_FILMS_QUERY, count);
        for (Film film : films) {
            film.setLikesCount(getLikeListsByFilmId(film.getId()));
        }
        return films.stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .toList();
    }

    @Override
    public Film getFilm(long id) {
        log.debug("FilmDbStorage. getFilm. с id {}", id);
        Film film = findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> {
            log.warn("Storage, getFilm, фильм с id {} не найден");
            throw new NotFoundFilmException(String.format("Фильм с id %d не найден", id), id);
        });

        if (genreService.containsGenre(film.getId())) {
            film.setGenres(genreService.getGenresByFilmId(film.getId()));
        }
        return film;
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

    private long getLikeListsByFilmId(long id) {
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
}
