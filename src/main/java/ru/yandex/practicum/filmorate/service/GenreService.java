package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundGenreException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(long id) {
        return genreStorage.getGenreById(id).orElseThrow(() -> {
            throw new NotFoundGenreException(String.format("Жанр по id не найден", id), id);
        });
    }

    public boolean containsGenre(long filmId) {
        return genreStorage.containsGenre(filmId);
    }

    public List<Genre> getGenresByFilmId(long filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }

    public void putGenre(long filmId, int genreId) {
        genreStorage.putGenre(filmId, genreId);
    }

    public void deleteGenre(long filmId) {
        genreStorage.deleteGenre(filmId);
    }
}
