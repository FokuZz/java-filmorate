package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;

import javax.validation.constraints.NotNull;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    private final Map<Integer, Set<User>> friends = new HashMap<>();

    private Set<User> setUsers;

    private Integer counterId = 1;
    private Integer deleteCounter = 0;


    @Override
    public User create(@NotNull User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsValue(user)) {
            if (user.getId() == null) {
                for (Map.Entry<Integer, User> u : users.entrySet()) {
                    if (u.getValue().equals(user)) {
                        log.warn("Пользователь уже был создан, была вызвана ошибка", UserController.class);
                        throw new HasAlreadyBeenCreatedException();
                    }
                }
            } else if (users.get(user.getId()).equals(user)) {
                log.warn("Пользователь уже был создан, была вызвана ошибка", UserController.class);
                throw new HasAlreadyBeenCreatedException();
            }
        }
        if (deleteCounter != 0) {     // Присваиваем прошлый id который мы удалили
            user.setId(deleteCounter);
            deleteCounter = 0;
        } else {
            user.setId(counterId++);
        }
        friends.put(user.getId(),new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(User user) {
        if (users.containsValue(user)) {
            deleteCounter = user.getId();
            users.remove(user.getId());
        }
        log.warn("Удаление несуществующего фильма не произошло", UserController.class);
        throw new HasNoBeenFoundException();
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            log.warn("Обновление несуществующего пользователя не произошло", UserController.class);
            throw new HasNoBeenFoundException();
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clear() {
        counterId = 1;
        deleteCounter = 0;
        users.clear();
        friends.clear();
    }

    @Override
    public User get(Integer userId) {
        if (!users.containsKey(userId)){
            log.warn("Поиск не произошёл из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return users.get(userId);
    }

    @Override
    public Set<User> createFriend(Integer userId, Integer friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            log.warn("Добавление в друзья не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = new HashSet<>(friends.get(userId));
        setUsers.add(users.get(friendId));
        friends.put(userId, setUsers);
        return friends.get(userId);
    }

    @Override
    public Set<User> deleteFriend(Integer userId, Integer friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            log.warn("Удаление из друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = friends.getOrDefault(userId, new HashSet<>());
        if (!setUsers.isEmpty()) {
            setUsers.removeIf(user -> user.getId().equals(friendId));
        }

        return setUsers;
    }

    @Override
    public Set<User> getAllFriends(Integer userId) {
        if (!users.containsKey(userId)) {
            log.warn("Получение списка друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return friends.get(userId);
    }

    @Override
    public Set<User> getAllCommonFriends(Integer userId, Integer userId2) {
        if (!users.containsKey(userId) || !users.containsKey(userId2)) {
            log.warn("Получение общих друзей не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }

        setUsers = new HashSet<>(friends.getOrDefault(userId, new HashSet<>()));
        setUsers.retainAll(friends.getOrDefault(userId2, new HashSet<>()));
        return setUsers;
    }
}
