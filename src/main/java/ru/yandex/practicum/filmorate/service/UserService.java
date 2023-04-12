package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage storage;

    @SneakyThrows
    public User create(@Valid @NotNull User user) {
        return storage.create(user);
    }

    public User update(@Valid @NotNull User user) {
        return storage.update(user);
    }

    public User delete(@Valid @NotNull User user) {
        return storage.delete(user);
    }

    public List<User> get() {
        return storage.getAll();
    }

    public User get(@Valid @NotNull Integer userId) {
        return storage.get(userId);
    }

    public Set<User> addFriend(
            @NotNull Integer userId,
            @NotNull Integer friendId)
    {
        return storage.createFriend(userId, friendId);
    }

    public Set<User> deleteFriend(
            @NotNull Integer userId,
            @NotNull Integer friendId)
    {
        return storage.deleteFriend(userId, friendId);
    }

    public Set<User> getAllFriends(@NotNull Integer userId) {
        return storage.getAllFriends(userId);
    }

    public void deleteAll() {
        storage.clear();
    }

    public Set<User> getCommonFriends(
            @NotNull Integer userId1,
            @NotNull Integer userId2)
    {
        return storage.getAllCommonFriends(userId1, userId2);
    }

}
