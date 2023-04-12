package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User create(User user);

    User delete(User user);

    User update(User user);

    List<User> getAll();

    void clear();

    User get(Integer userId);

    Set<User> createFriend(Integer userId, Integer friendId);

    Set<User> deleteFriend(Integer userId, Integer friendId);

    Set<User> getAllFriends(Integer userId);

    Set<User> getAllCommonFriends(Integer userId, Integer userId2);
}
