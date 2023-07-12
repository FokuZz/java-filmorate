package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDbStorage {

    Film create(Film film);

    boolean addFilmGenre(int filmId, int genreId);

    boolean deleteFilmGenre(int filmId, int genreId);

    boolean deleteAllFilmGenre(int filmId);

    Film delete(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film get(Integer filmId);

}
