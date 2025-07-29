package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class NotFoundMpaException extends RuntimeException {
    private final long id;

    public NotFoundMpaException(String message, long id) {
        super(message);
        this.id = id;
    }
}
