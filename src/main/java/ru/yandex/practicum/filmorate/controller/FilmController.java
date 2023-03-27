package ru.yandex.practicum.filmorate.controller;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer,Film> films = new HashMap<>();
    private Integer counterId = 1;

    @PostMapping
    @SneakyThrows
    public Film createFilm(@Valid @RequestBody Film film) {
        validation(film);
        film.setId(counterId++);
        if(films.containsValue(film)){
            log.warn("Фильм уже был создан, была вызвана ошибка",FilmController.class);
            throw new HasAlreadyBeenCreatedException();
        }
        films.put(film.getId(),film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validation(film);
        if(films.containsKey(film.getId())) {
            films.put(film.getId(),film);
            return film;
        } else {
            log.warn("Обновление несуществующего фильма не произошло",UserController.class);
            throw new HasNoBeenCreatedException();
        }
    }

    @GetMapping
    public Map<Integer,Film> getFilms() {
        return films;
    }
    @SneakyThrows
    public void validation(@Valid Film film){ // Валидация
            if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
                log.warn("День релиза фильма раньше 1895 года",FilmController.class);
                throw new RelaseDateEarlyThanNecessaryException();
            }
    }
}
