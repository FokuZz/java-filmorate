package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer counterId = 1;
    private Integer deleteCounter = 0;

    @Override
    public Film create(Film film) {
        if (films.containsValue(film)) {
            log.warn("Фильм уже был создан, была вызвана ошибка", FilmController.class);
            throw new HasAlreadyBeenCreatedException();
        }
        if (deleteCounter != 0) {     // Присваиваем прошлый id который мы удалили
            film.setId(deleteCounter);
            deleteCounter = 0;
        } else {
            film.setId(counterId++);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        if (films.containsValue(film)) {
            deleteCounter = film.getId();
            films.remove(film.getId());
        }
        log.warn("Удаление несуществующего фильма не произошло", UserController.class);
        throw new HasNoBeenFoundException();
    }

    @Override
    public void clear() {
        counterId = 1;
        deleteCounter = 0;
        films.clear();
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Обновление несуществующего фильма не произошло", UserController.class);
            throw new HasNoBeenFoundException();
        }
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Нахождение не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return films.get(filmId);
    }
}
