package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs));
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM GENRE WHERE genre_id = ?";
        List<Genre> genreList = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs), genreId);
        if (genreList.isEmpty()) {
            log.warn("Genre не был найден, id = {} неверный", genreId);
            throw new HasNoBeenFoundException();
        }
        return genreList.get(0);
    }

    @Override
    public List<Genre> getGenreByFilmId(int filmId) {
        String sql = "SELECT g.* FROM film_genre AS fg JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToGenre(rs), filmId);
    }

    private Genre mapRowToGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }
}
