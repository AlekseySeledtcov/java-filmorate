package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        log.info("Добавление фильма с id {}", film.getId());
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film deleteFilm(Film film) {
        log.info("Удаление фильма с id {}", film.getId());
        return films.remove(film);
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление фильма с id {}", film.getId());
        return films.put(film.getId(), film);
    }

    @Override
    public Film getFilm(long id) {
        log.info("Получение фильма с id {}", id);
        return films.get(id);
    }

    @Override
    public List<Film> getFilmsList() {
        log.info("Получение списка фильмов");
        return films.values().stream().toList();
    }

    @Override
    public boolean containsFilm(long id) {
        log.info("Проверка наличия фильма с id {} в хранилище", id);
        return films.containsKey(id);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
