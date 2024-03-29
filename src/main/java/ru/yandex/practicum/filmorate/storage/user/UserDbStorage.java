package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDbStorage {
    User create(User user);

    User delete(User user);

    User update(User user);

    List<User> getAll();

    User get(Integer userId);
}
