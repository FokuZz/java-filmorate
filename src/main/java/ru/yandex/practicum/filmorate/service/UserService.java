package ru.yandex.practicum.filmorate.service;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.BirthdayInFutureException;
import ru.yandex.practicum.filmorate.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.exception.HasNoBeenCreatedException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Validated
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer counterId = 1;

    @SneakyThrows
    public User create(User user) {
        user = validation(user);
        user.setId(counterId++);
        if (users.containsValue(user)) {
            log.warn("Пользователь уже был создан, была вызвана ошибка", UserController.class);
            throw new HasAlreadyBeenCreatedException();
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        user = validation(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            log.warn("Обновление несуществующего пользователя не произошло", UserController.class);
            throw new HasNoBeenCreatedException();
        }
    }


    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @SneakyThrows
    public User validation(@Valid @NotNull User user) {
        if (users.containsValue(user)) {
            if (user.getId() == null) {
                for (Map.Entry<Integer, User> u : users.entrySet()) {
                    if (u.getValue().equals(user)) {
                        throw new HasAlreadyBeenCreatedException();
                    }
                }
            } else if (users.get(user.getId()).equals(user)) {
                throw new HasAlreadyBeenCreatedException();
            }

        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем", UserController.class);
            throw new BirthdayInFutureException();
        }
        return user;
    }
}
