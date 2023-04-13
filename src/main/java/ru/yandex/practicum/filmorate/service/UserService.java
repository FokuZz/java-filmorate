package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage storage;
    private final Map<Integer, Set<User>> friends = new HashMap<>();
    private Set<User> setUsers;

    @SneakyThrows
    public User create(@Valid @NotNull User user) {
        storage.create(user);
        friends.put(user.getId(), new HashSet<>());
        return user;
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
            @NotNull Integer friendId) {
        if (!storage.getUsers().containsKey(userId) || !storage.getUsers().containsKey(friendId)) {
            log.warn("Добавление в друзья не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = new HashSet<>(friends.get(friendId));
        setUsers.add(storage.getUsers().get(userId));
        friends.put(friendId, setUsers);
        setUsers = new HashSet<>(friends.get(userId));
        setUsers.add(storage.getUsers().get(friendId));
        friends.put(userId, setUsers);
        return friends.get(userId);
    }

    public Set<User> deleteFriend(
            @NotNull Integer userId,
            @NotNull Integer friendId) {
        if (!storage.getUsers().containsKey(userId) || !storage.getUsers().containsKey(friendId)) {
            log.warn("Удаление из друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = friends.getOrDefault(userId, new HashSet<>());
        if (!setUsers.isEmpty()) {
            setUsers.removeIf(user -> user.getId().equals(friendId));
        }

        return setUsers;
    }

    public Set<User> getAllFriends(@NotNull Integer userId) {
        if (!storage.getUsers().containsKey(userId)) {
            log.warn("Получение списка друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return friends.get(userId);
    }

    public void deleteAll() {
        friends.clear();
        storage.clear();
    }

    public Set<User> getCommonFriends(
            @NotNull Integer userId1,
            @NotNull Integer userId2) {
        if (!storage.getUsers().containsKey(userId1) || !storage.getUsers().containsKey(userId2)) {
            log.warn("Получение общих друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }

        setUsers = new HashSet<>(friends.getOrDefault(userId1, new HashSet<>()));
        setUsers.retainAll(friends.getOrDefault(userId2, new HashSet<>()));
        return setUsers;
    }

}
