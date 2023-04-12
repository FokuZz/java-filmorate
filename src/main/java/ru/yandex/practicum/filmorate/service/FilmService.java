package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class FilmService {

    private final InMemoryFilmStorage storage;

    public Film create(@Valid @NotNull Film film) {
        return storage.create(film);
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

    public Film get(@NotNull Integer filmId){
        return storage.get(filmId);
    }

    public Set<User> addLike(@NotNull Integer filmId,
                             @NotNull Integer userId) {
        return storage.createLike(filmId,userId);
    }

    public Set<User> deleteLike(@NotNull Integer filmId,
                                @NotNull Integer userId) {
        return storage.deleteLike(filmId,userId);
    }

    public void deleteAll(){
        storage.clear();
    }

    public Set<Film> getTopLiked(@NotNull @Positive(message = "Count must be positive") Integer count) {
        return storage.getTop(count);
    }
}
