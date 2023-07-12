package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping()
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService service;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return service.create(user);
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        return service.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public List<Integer> addFriend(
            @PathVariable("id") @NotNull Integer userId,
            @PathVariable("friendId") @NotNull Integer friendId) {
        return service.addFriend(userId, friendId);
    }

    @DeleteMapping("/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User delete(@Valid @RequestBody User user) {
        return service.delete(user);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public List<Integer> deleteFriend(
            @PathVariable("id") @NotNull Integer userId,
            @PathVariable("friendId") @NotNull Integer friendId) {
        return service.deleteFriend(userId, friendId);
    }

    @GetMapping("/users")
    public List<User> get() {
        return service.get();
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable("id") @NotNull Integer userId) {
        return service.get(userId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") @NotNull Integer userId) {
        return service.getAllFriends(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(
            @PathVariable("id") @NotNull Integer userId1,
            @PathVariable("otherId") @NotNull Integer userId2) {
        return service.getCommonFriends(userId1, userId2);
    }
}
