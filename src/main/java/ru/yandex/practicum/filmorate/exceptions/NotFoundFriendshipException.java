package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class NotFoundFriendshipException extends RuntimeException {
    private final long id;
    private final long friendId;

    public NotFoundFriendshipException(String message, long id, long friendId) {
        super(message);
        this.id = id;
        this.friendId = friendId;
    }
}
