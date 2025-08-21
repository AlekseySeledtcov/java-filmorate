package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

@Getter
public class NotFoundReactionException extends RuntimeException {
    private final Long reviewId;
    private final Long userId;

    public NotFoundReactionException(final String message, final Long reviewId, final Long userId) {
        super(message);
        this.reviewId = reviewId;
        this.userId = userId;
    }

    public String getDetailMessage() {
        return getMessage() + " - reviewId: " + reviewId + ", userId: " + userId;
    }
}