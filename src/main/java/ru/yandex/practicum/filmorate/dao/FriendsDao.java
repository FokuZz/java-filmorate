package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FriendsDao {

    List<Integer> getFriendsByUserId(int id);

    List<Integer> getFriends();

    boolean addFriend(int userId, int friendId);

    boolean updateFriend(int userId, int friendId, boolean status);

    boolean deleteFriend(int userId, int friendId);
}
