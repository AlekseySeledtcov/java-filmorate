package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class NotFoundReviewException extends RuntimeException {
    private final Long id;

    public NotFoundReviewException(final String message, final Long id) {
        super(message);
        this.id = id;
    }

    public String getDetailMessage() {
        return getMessage() + " = " + id;
    }
}