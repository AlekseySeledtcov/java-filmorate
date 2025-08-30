package ru.yandex.practicum.filmorate.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private final long id;

    public EntityNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public String getDetailMessage() {
        return getMessage() + " по id = " + id;
    }
}
