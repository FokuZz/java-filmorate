package ru.yandex.practicum.filmorate.controller;



import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_VALUE)
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }


}
