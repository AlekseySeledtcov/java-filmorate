package ru.yandex.practicum.filmorate.storage;

public interface FriendListStorage {

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    boolean containsFriend(long userId, long friendId);
}
