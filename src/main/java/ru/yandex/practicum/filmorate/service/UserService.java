package ru.yandex.practicum.filmorate.service;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.exception.HasNoBeenCreatedException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(counterId++);
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
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
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

}
