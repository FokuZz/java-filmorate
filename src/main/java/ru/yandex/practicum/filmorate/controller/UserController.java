package ru.yandex.practicum.filmorate.controller;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;


import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer,User> users = new HashMap();
    private Integer counterId = 1;

    @PostMapping
    @SneakyThrows
    public User createUser(@Valid @RequestBody User user) {
        user = validation(user);
        user.setId(counterId++);
        if(users.containsValue(user)) {
            log.warn("Пользователь уже был создан, была вызвана ошибка",UserController.class);
            throw new HasAlreadyBeenCreatedException();
        }
        users.put(user.getId(),user);
    return user;
    }
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        user = validation(user);
        if(users.containsKey(user.getId())) {
            users.put(user.getId(),user);
            return user;
        } else {
            log.warn("Обновление несуществующего пользователя не произошло",UserController.class);
            throw new HasNoBeenCreatedException();
        }
    }

    @GetMapping
    public Map<Integer,User> getUsers() {
        return users;
    }

    @SneakyThrows
    public User validation(@Valid User user){ // Валидация
        user.setLogin(user.getLogin().trim());          // (вместо валидации) Теперь он не содержит лишних пробелов
        if(users.containsValue(user)){
            if(user.getId() == null){
                for(Map.Entry<Integer,User> u : users.entrySet()) {
                    if (u.getValue().equals(user)) {
                        throw new HasAlreadyBeenCreatedException();
                    }
                }
                } else if(users.get(user.getId()).equals(user)){
                throw new HasAlreadyBeenCreatedException();
            }

        }
        if(user.getName() == null) {user.setName(user.getLogin());}
        if(user.getName().isEmpty()) {user.setName(user.getLogin());}
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем",UserController.class);
            throw new BirthdayInFutureException();
        }
        return user;
    }

}
