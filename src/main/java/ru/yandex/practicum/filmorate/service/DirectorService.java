package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorDbStorage;

    public DirectorService(DirectorStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public List<Director> getDirectors() {
        return directorDbStorage.getDirectors();
    }

    public Director getDirectorById(long id) {
        return directorDbStorage.getDirectorById(id).orElseThrow(() -> {
            log.warn("getDirectorById, режиссер с id {} не найден", id);
            return new EntityNotFoundException("Режиссер не найден", id);
        });
    }

    public Director postDirector(Director director) {
        return directorDbStorage.postDirector(director);
    }

    public Director putDirector(Director director) {
        if (!directorDbStorage.containsDirectorById(director.getId())) {
            log.warn("putDirector, режиссер с id {} не найден", director.getId());
            throw new EntityNotFoundException("Режиссер не найден", director.getId());
        }
        return directorDbStorage.putDirector(director);
    }

    public void deleteDirector(long id) {
        if (!directorDbStorage.containsDirectorById(id)) {
            log.warn("deleteDirector, режиссер с id {} не найден", id);
            throw new EntityNotFoundException("Режиссер не найден по id", id);
        }
        directorDbStorage.deleteDirector(id);
    }

    public void deleteDirectorsFromFilm(long id) {
        directorDbStorage.deleteDirectorsFromFilm(id);
    }

    public void putDirectorsToFilm(List<Director> directors, long filmId) {
        directors.forEach(director -> directorDbStorage.putDirectorsToFilm(filmId, director.getId()));
    }

    public List<Director> getDirectorsByFilmId(long id) {
        return directorDbStorage.getDirectorsByFilmId(id);
    }
}