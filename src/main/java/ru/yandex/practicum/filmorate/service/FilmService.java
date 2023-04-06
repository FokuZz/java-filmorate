package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.exception.HasNoBeenCreatedException;
import ru.yandex.practicum.filmorate.exception.RelaseDateEarlyThanNecessaryException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Validated
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer counterId = 1;


    public Film create(@Valid Film film) {
        validation(film);
        film.setId(counterId++);
        if (films.containsValue(film)) {
            log.warn("Фильм уже был создан, была вызвана ошибка", FilmController.class);
            throw new HasAlreadyBeenCreatedException();
        }
        films.put(film.getId(), film);
        return film;
    }

    public Film update(@Valid Film film) {
        validation(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Обновление несуществующего фильма не произошло", UserController.class);
            throw new HasNoBeenCreatedException();
        }
    }

    public List<Film> get() {
        return new ArrayList<>(films.values());
    }


    private void validation(Film film) { // Валидация
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("День релиза фильма раньше 1895 года", FilmController.class);
            throw new RelaseDateEarlyThanNecessaryException();
        }
    }
}
