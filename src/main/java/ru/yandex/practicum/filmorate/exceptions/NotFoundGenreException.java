package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundGenreException extends RuntimeException {
    long id;

    public NotFoundGenreException(String message, long id) {
        super(message);
        this.id = id;
    }
}
