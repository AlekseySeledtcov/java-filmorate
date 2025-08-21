package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundEntityByIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

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
        return directorDbStorage.getDirectorById(id);
    }

    public Director postDirector(Director director) {
        return directorDbStorage.postDirector(director);
    }

    public Director putDirector(Director director) {
        if (!directorDbStorage.containsDirectorById(director.getId())) {
            throw new NotFoundEntityByIdException("Режиссер не найден", director.getId());
        }
        return directorDbStorage.putDirector(director);
    }

    public void deleteDirector(long id) {
        directorDbStorage.deleteDirector(id);
    }

    public void deleteDirectorsFromFilm(long id) {
        directorDbStorage.deleteDirectorsFromFilm(id);
    }

    public void putDirectorsToFilm(List<Director> directors, long filmId) {
        for (int i = 0; i < directors.size(); i++) {
            directorDbStorage.putDirectorsToFilm(directors.get(i).getId(), filmId);
        }
    }

    public List<Director> getDirectorsByFilmId(long id) {
        return directorDbStorage.getDirectorsByFilmId(id);
    }
}
