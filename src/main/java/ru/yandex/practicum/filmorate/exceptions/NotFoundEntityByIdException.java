package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundEntityByIdException extends RuntimeException {
    private final long id;

    public NotFoundEntityByIdException(String message, long id) {
        super(message);
        this.id = id;
    }

    public String getDetailMessage() {
        return getMessage() + " по id = " + id;
    }
}
