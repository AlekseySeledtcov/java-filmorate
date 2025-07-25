package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundGenreException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

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

    public void putGenre(List<Genre> genres, long id) {
        genres = genres.stream()
                .distinct()
                .collect(Collectors.toList());
        for (int i = 0; i < genres.size(); i++) {
            if (genres.get(i).getId() > genreStorage.getAllGenres().size()) {
                log.warn("Жанр с индексом id {} в базе не найден", genres.get(i).getId());
                throw new NotFoundGenreException(String.format("Жанр с индексом id %d в базе не найден",
                        genres.get(i).getId()), genres.get(i).getId());
            }
            log.debug("GenreService. Обновление жанров");
            if (genres.get(i).getId() != 0 && genres.get(i).getId() != null) {
                genreStorage.putGenre(id, genres.get(i).getId());
            }
        }
    }

    public void deleteGenre(long filmId) {
        genreStorage.deleteGenre(filmId);
    }
}
