package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;

    private final GenreDao genreDao;

    @Override
    public Film create(Film film) {
        if (getAll().contains(film)) {
            log.warn("Фильм уже был создан, была вызвана ошибка", FilmController.class);
            throw new HasAlreadyBeenCreatedException();
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(filmid -> addFilmGenre(id, filmid.getId()));
        }

        log.debug("Фильм {} добавлен с жанрами", film);
        return film;
    }

    @Override
    public boolean addFilmGenre(int filmId, int genreId) {
        String sql = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, filmId, genreId) > 0;
    }

    @Override
    public boolean deleteFilmGenre(int filmId, int genreId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
        return jdbcTemplate.update(sql, filmId, genreId) > 0;
    }

    @Override
    public boolean deleteAllFilmGenre(int filmId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? ";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public Film delete(Film film) {
        if (getAll().contains(film)) {
            String sql = "DELETE FROM films WHERE film_id = ? AND name = ? AND description = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNub) -> mapRowToFilm(rs),
                    film.getId(), film.getName(), film.getDescription());
        }
        log.warn("Удаление несуществующего фильма не произошло", UserController.class);
        throw new HasNoBeenFoundException();
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, RELEASE_DATE = ?, description = ?, duration = ?, RATING_ID = ?, RATE = ? " +
                "WHERE film_id = ?";
        if (jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getMpa().getId(), film.getRate(), film.getId()) > 0) {
            deleteAllFilmGenre(film.getId());
            if (!film.getGenres().isEmpty()) {
                film.getGenres().forEach(genre -> addFilmGenre(film.getId(), genre.getId()));
            }

            return film;
        } else {
            log.warn("Обновление несуществующего фильма не произошло", UserController.class);
            throw new HasNoBeenFoundException();
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, r.name AS rating_name FROM films AS f JOIN RATING AS r on f.RATING_ID = R.RATING_ID";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs))
                .stream()
                .peek(film -> genreDao.getGenreByFilmId(film.getId())
                        .forEach(film::addGenre))
                .collect(Collectors.toList());
    }

    @Override
    public Film get(Integer filmId) {
        String sql = "SELECT f.*, r.name AS rating_name FROM films AS f JOIN RATING AS r on f.RATING_ID = R.RATING_ID WHERE film_id = ?";
        List<Film> filmList = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), filmId);
        if (filmList.size() == 0) {
            log.warn("Ничего не нашло из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        genreDao.getGenreByFilmId(filmId).forEach(filmList.get(0)::addGenre);
        return filmList.get(0);
    }

    public boolean containsKey(int id) {
        String sql = "SELECT f.*, r.name AS rating_name FROM films AS f JOIN RATING AS r on f.RATING_ID = R.RATING_ID WHERE film_id = ?";
        if (jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), id).isEmpty()) {
            log.warn("Такого айди фильма нет в базе данных id = " + id, FilmDbStorageImpl.class);
            throw new HasNoBeenFoundException();
        }
        return true;
    }

    public Film mapRowToFilm(ResultSet rs) throws SQLException {

        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int ratingId = rs.getInt("rating_id");
        String ratingName = rs.getString("rating_name");
        Mpa mpa = Mpa.builder()
                .id(ratingId)
                .name(ratingName)
                .build();

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .build();
    }
}
