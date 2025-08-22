package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendsList {
    private long userId;
    private long friendId;
    private String status; // PENDING или CONFIRMED

    // Добавляем геттер для статуса, который используется в методе getFriendshipStatus
    public String getStatus() {
        return status;
    }

    public FriendsList(long userId, long friendId, String status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }
}