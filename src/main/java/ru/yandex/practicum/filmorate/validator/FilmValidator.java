package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private final int maxFilmLength = 200;
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

    public void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не пожет быть пустым");
        }
        if (film.getDescription().length() > maxFilmLength) {
            throw new ValidationException("Превышена максимальная длинна нахвания фильма");
        }
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1985 г.");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
