package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorageImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserService {

    private final FriendsDao friendsDao;
    private final UserDbStorageImpl storage;

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

    public List<Integer> addFriend(
            @NotNull Integer userId,
            @NotNull Integer friendId) {
        if (!storage.containsKey(userId) || !storage.containsKey(friendId)) {
            log.warn("Добавление в друзья не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        boolean isUser1FriendToUser2 = friendsDao.getFriendsByUserId(userId).contains(friendId);
        boolean isUser2FriendToUser1 = friendsDao.getFriendsByUserId(friendId).contains(userId);
        if (!isUser1FriendToUser2 && !isUser2FriendToUser1) {
            friendsDao.addFriend(userId, friendId);
        } else if (isUser1FriendToUser2 && !isUser2FriendToUser1) {
            friendsDao.updateFriend(friendId, userId, true);
        } else {
            log.debug("id = {] уже в друзьях у id = {}", userId, friendId);
        }

        return friendsDao.getFriendsByUserId(userId);
    }

    public List<Integer> deleteFriend(
            @NotNull Integer userId,
            @NotNull Integer friendId) {
        if (!storage.containsKey(userId) || !storage.containsKey(friendId)) {
            log.warn("Удаление из друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        if (friendsDao.deleteFriend(userId, friendId)) {
            return friendsDao.getFriends();
        }
        log.warn("Удаление из друзей не произошло, id не найден в списке друзей", UserController.class);
        throw new HasNoBeenFoundException();
    }

    public List<User> getAllFriends(@NotNull Integer userId) {
        if (!storage.containsKey(userId)) {
            log.warn("Получение списка друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return friendsDao.getFriendsByUserId(userId).stream()
                .map(this::get).collect(Collectors.toList());
    }

    public Set<User> getCommonFriends(
            @NotNull Integer userId1,
            @NotNull Integer userId2) {
        if (!storage.containsKey(userId1) || !storage.containsKey(userId2)) {
            log.warn("Получение общих друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return friendsDao.getFriendsByUserId(userId1).stream()
                .filter(friendsDao.getFriendsByUserId(userId2)::contains)
                .map(this::get)
                .collect(Collectors.toSet());
    }

}
