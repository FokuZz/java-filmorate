package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getRatings() {
        String sql = "SELECT * FROM rating ORDER BY rating_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeRowToRating(rs));
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        String sql = "SELECT * FROM rating WHERE rating_id = ? ORDER BY rating_id";
        List<Mpa> mpaList = jdbcTemplate.query(sql, (rs, rowNum) -> makeRowToRating(rs), ratingId);
        if (mpaList.isEmpty()) {
            log.warn("Mpa не был найден, id = {} неверный", ratingId);
            throw new HasNoBeenFoundException();
        }
        return mpaList.get(0);
    }

    private Mpa makeRowToRating(ResultSet rs) throws SQLException {
        int id = rs.getInt("rating_id");
        String name = rs.getString("name");
        return Mpa.builder()
                .id(id)
                .name(name)
                .build();
    }
}
