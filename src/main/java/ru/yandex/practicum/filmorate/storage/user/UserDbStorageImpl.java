package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;

import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(@NotNull User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.warn("Имя не было указано, был использован логин.", UserController.class);
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(id);
        log.debug("Юзер {} добавлен", user);
        return user;
    }


    public User delete(User user) {
        if (getAll().contains(user)) {
            String sql = "DELETE FROM users WHERE user_id = ? AND email = ? AND login = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToUser(rs), user.getId(), user.getEmail(), user.getLogin());
        }
        log.warn("Удаление несуществующего фильма не произошло", UserController.class);
        throw new HasNoBeenFoundException();
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        if (jdbcTemplate.update(sql, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId()) > 0) {
            return user;
        } else {
            log.warn("Обновление несуществующего пользователя не произошло", UserController.class);
            throw new HasNoBeenFoundException();
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users ORDER BY user_id DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User get(Integer userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> usersList = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), userId);
        if (usersList.size() == 0) {
            log.warn("Поиск не произошёл из-за неверного ID", UserController.class);
            throw new HasNoBeenFoundException();
        }
        return usersList.get(0);
    }

    public boolean containsKey(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        if (jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), userId).isEmpty()) {
            log.warn("Юзер не был найден по этому айди id = " + userId, UserDbStorageImpl.class);
            throw new HasNoBeenFoundException();
        }
        return true;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .login(login)
                .birthday(birthday)
                .build();
    }
}
