package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FilmController {

    private final FilmService service;

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return service.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Set<User> like(@PathVariable("id") @NotNull Integer filmId
            , @PathVariable() @NotNull Integer userId) {
        return service.addLike(filmId, userId);
    }

    @DeleteMapping("/films")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Film delete(@Valid @RequestBody Film film) {
        return service.delete(film);
    }

    @DeleteMapping("/films/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        service.deleteAll();
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Set<User> deleteLike(
            @PathVariable("id") @NotNull Integer filmId
            , @PathVariable() @NotNull Integer userId) {
        return service.deleteLike(filmId, userId);
    }

    @GetMapping(value = "/films", consumes = {}, produces = {})
    public List<Film> get() {
        return service.get();
    }

    @GetMapping(value = "/films/{id}", consumes = {}, produces = {})
    public Film get(
            @PathVariable("id") @NotNull Integer filmsId) {
        return service.get(filmsId);
    }

    @GetMapping(value = "/films/popular", consumes = {}, produces = {})
    public Set<Film> getTop(@RequestParam(defaultValue = "10", name = "count") @NotNull Integer count) {
        return service.getTopLiked(count);
    }

}
