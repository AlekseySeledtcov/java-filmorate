package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class NotFoundFilmException extends RuntimeException {
    private final Long id;

    public NotFoundFilmException(String message, Long id) {
        super(message);
        this.id = id;
    }

    public String getDetailMessage() {
        return getMessage() + " = " + id;
    }
}
