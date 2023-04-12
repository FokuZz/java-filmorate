package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final InMemoryUserStorage userStorage;

    private final Map<Integer, Film> films = new HashMap<>();

    private final Map<Integer, Set<User>> likes = new HashMap<>();
    Set<User> setUsers;
    private List<Map.Entry<Integer, Set<User>>> entries;
    private Set<Film> topFilms;
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
        likes.put(film.getId(), new HashSet<>());
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
        likes.clear();
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

    @Override
    public Set<User> createLike(Integer filmId, Integer userId) {
        if (!userStorage.getUsers().containsKey(userId) || !films.containsKey(filmId)) {
            log.warn("Создания лайка не произошло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        setUsers = likes.getOrDefault(filmId, new HashSet<>());
        setUsers.add(userStorage.getUsers().get(userId));
        likes.put(filmId, setUsers);
        return likes.get(filmId);
    }

    @Override
    public Set<User> deleteLike(Integer filmId, Integer userId) {
        if (!userStorage.getUsers().containsKey(userId) || !films.containsKey(filmId)) {
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

    @Override
    public Set<Film> getTop(Integer count) {
        entries = new ArrayList<>(likes.entrySet());
        entries.sort(Comparator.comparingInt(entry -> entry.getValue().size() * -1)); // -1 для сортировки по убыванию
        topFilms = new HashSet<>();
        int i = 0;
        for (Map.Entry<Integer, Set<User>> entry : entries) {
            topFilms.add(films.get(entry.getKey()));
            i++;
            if (i >= count) {
                break;
            }
        }
        return topFilms;
    }
}
