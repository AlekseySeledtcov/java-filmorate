package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class NotFoundUserByFriendIdException extends RuntimeException {
    private final Long friendId;

    public NotFoundUserByFriendIdException(final String message, final Long friendId) {
        super(message);
        this.friendId = friendId;
    }

    public String getDetailMessage() {
        return getMessage() + " = " + friendId;
    }
}
