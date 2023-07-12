package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorageImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorageImpl storage;
    private final UserDbStorageImpl userStorage;

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

    public Film get(@NotNull Integer filmId) {
        return storage.get(filmId);
    }


    public List<Integer> getLikeByFilmId(long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id =?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId);
    }

    public boolean addLike(
            @NotNull Integer filmId,
            @NotNull Integer userId) {
        if (!userStorage.containsKey(userId) || !storage.containsKey(filmId)) {
            log.warn("Создания лайка не произошло из-за неверного ID", FilmService.class);
            throw new HasNoBeenFoundException();
        }
        String sql = "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;

    }

    public boolean deleteLike(
            @NotNull Integer filmId,
            @NotNull Integer userId) {
        if (!userStorage.containsKey(userId) || !storage.containsKey(filmId)) {
            log.warn("Создания лайка не произошло из-за неверного ID", FilmService.class);
            throw new HasNoBeenFoundException();
        }
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    public List<Film> getTopLiked(@NotNull @Positive(message = "Count must be positive") Integer count) {
        String sql = "SELECT f.*, r.name AS rating_name FROM FILMS AS f " +
                "JOIN RATING AS r ON f.RATING_ID = r.RATING_ID " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(user_id) AS total_likes " +
                "FROM LIKES GROUP BY FILM_ID ORDER BY total_likes DESC) AS list ON f.FILM_ID = list.FILM_ID " +
                "ORDER BY list.total_likes DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> storage.mapRowToFilm(rs), count);
    }
}
