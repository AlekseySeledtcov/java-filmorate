package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Qualifier("InMemoryFilmStorage")
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
    public List<Film> getFilms() {
        log.info("Получение списка фильмов");
        return films.values().stream().toList();
    }

    @Override
    public boolean containsFilmById(long id) {
        log.info("Проверка наличия фильма с id {} в хранилище", id);
        return films.containsKey(id);
    }

    @Override
    public List<Film> getPopularFilmList(int count) {
        log.info("Получение {} популярных фильмов", count);
        return films.values().stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsFilmByName(String name) {
        log.info("Проверка наличия фильма с именем {}", name);
        return films.values().stream()
                .anyMatch(film -> film.getName().equalsIgnoreCase(name));
    }

    @Override
    public List<Film> getFilmsLikedByUser(long userId) {
        log.info("Получение фильмов, которые понравились пользователю ID: {}", userId);
        // В in-memory реализации возвращаем все фильмы
        return films.values().stream()
                .collect(Collectors.toList());
    }

//    @Override
//    public List<Film> getFilmsNotLikedByUser(long userId) {
//        log.info("Получение фильмов, которые не понравились пользователю ID: {}", userId);
//        // В in-memory реализации возвращаем пустой список
//        return List.of();
//    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
