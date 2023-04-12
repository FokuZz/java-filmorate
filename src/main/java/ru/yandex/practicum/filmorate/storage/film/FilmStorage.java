package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Film create(Film film);

    Film delete(Film film);

    void clear();

    Film update(Film film);

    List<Film> getAll();

    Film get(Integer filmId);

    Set<User> createLike(Integer filmId, Integer userId);

    Set<User> deleteLike(Integer filmId, Integer userId);

    Set<Film> getTop(Integer count);

}
