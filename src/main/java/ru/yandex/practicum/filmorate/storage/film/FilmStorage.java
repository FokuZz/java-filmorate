package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film delete(Film film);

    void clear();

    Film update(Film film);

    List<Film> getAll();

    Film get(Integer filmId);

}
