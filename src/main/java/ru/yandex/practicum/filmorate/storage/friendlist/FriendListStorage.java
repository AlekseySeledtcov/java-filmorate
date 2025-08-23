package ru.yandex.practicum.filmorate.storage.friendlist;

import ru.yandex.practicum.filmorate.model.FriendsList;

import java.util.List;

public interface FriendListStorage {

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    boolean containsFriend(long userId, long friendId);

    void updateFriendshipStatus(long userId, long friendId, String status);

    List<FriendsList> getFriendsWithStatus(long userId, String status);

}
