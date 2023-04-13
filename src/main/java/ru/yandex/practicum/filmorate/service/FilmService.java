package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.*;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final InMemoryFilmStorage storage;
    private final InMemoryUserStorage userStorage;

    private final Map<Integer, Set<User>> likes = new HashMap<>();
    Set<User> setUsers;

    private List<Map.Entry<Integer, Set<User>>> entries;
    private Set<Film> topFilms;

    public Film create(@Valid @NotNull Film film) {
        storage.create(film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    public Film update(@Valid @NotNull Film film) {
        return storage.update(film);
    }

    public Film delete(@Valid @NotNull Film film) {
        return storage.delete(film);
    }

    public List<Film> get() {
        return storage.getAll();
    }

    public Film get(@NotNull Integer filmId) {
        return storage.get(filmId);
    }

    public Set<User> addLike(
            @NotNull Integer filmId,
            @NotNull Integer userId) {
        if (!userStorage.getUsers().containsKey(userId) || !storage.getFilms().containsKey(filmId)) {
            log.warn("Создания лайка не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = likes.getOrDefault(filmId, new HashSet<>());
        setUsers.add(userStorage.getUsers().get(userId));
        likes.put(filmId, setUsers);
        return likes.get(filmId);
    }

    public Set<User> deleteLike(
            @NotNull Integer filmId,
            @NotNull Integer userId) {
        if (!userStorage.getUsers().containsKey(userId) || !storage.getFilms().containsKey(filmId)) {
            log.warn("Создания лайка не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = likes.getOrDefault(filmId, new HashSet<>());
        if (!setUsers.isEmpty()) {
            setUsers.removeIf(user -> user.getId().equals(userId));
        }
        likes.put(filmId, setUsers);
        return likes.get(filmId);
    }

    public void deleteAll() {
        likes.clear();
        storage.clear();
    }

    public Set<Film> getTopLiked(@NotNull @Positive(message = "Count must be positive") Integer count) {
        entries = new ArrayList<>(likes.entrySet());
        entries.sort(Comparator.comparingInt(entry -> entry.getValue().size() * -1)); // -1 для сортировки по убыванию
        topFilms = new HashSet<>();
        int i = 0;
        for (Map.Entry<Integer, Set<User>> entry : entries) {
            topFilms.add(storage.getFilms().get(entry.getKey()));
            i++;
            if (i >= count) {
                break;
            }
        }
        return topFilms;
    }
}
