package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    List<Director> getDirectors();

    Optional<Director> getDirectorById(long id);

    Director postDirector(Director director);

    Director putDirector(Director director);

    void deleteDirector(long id);

    void deleteDirectorsFromFilm(long id);

    boolean containsDirectorById(long id);

    void putDirectorsToFilm(long directorId, long filmId);

    List<Director> getDirectorsByFilmId(long id);

}
