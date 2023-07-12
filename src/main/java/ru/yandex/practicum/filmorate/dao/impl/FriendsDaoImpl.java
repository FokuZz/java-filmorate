package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendsDao;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsDaoImpl implements FriendsDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> getFriendsByUserId(int id) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), id);
    }

    @Override
    public List<Integer> getFriends() {
        String sql = "SELECT * FROM friends";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"));
    }

    @Override
    public boolean addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends(user_id, friend_id, confirmed_friend) " +
                "VALUES (?,?,false)";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public boolean updateFriend(int userId, int friendId, boolean status) {
        String sql = "UPDATE friends SET CONFIRMED_FRIEND = ? WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sql, status, userId, friendId) > 0;
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }
}
