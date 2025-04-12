package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public Optional<User> getUserById(long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId)
                .stream()
                .findFirst();
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        String friendsSql = "SELECT friend_id, status FROM friends WHERE user_id = ?";
        HashMap<Long, FriendshipStatus> friends = new HashMap<>();
        jdbcTemplate.query(friendsSql, (rs2, rowNum) -> {
            friends.put(rs2.getLong("friend_id"),
                    rs2.getString("status").equals("CONFIRMED") ? FriendshipStatus.CONFIRMED : FriendshipStatus.UNCONFIRMED);
            return null;
        }, user.getId());
        user.setFriends(friends);

        return user;
    }

    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, "UNCONFIRMED");

        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, friendId, userId);
        if (count > 0) {
            String updateSql = "UPDATE friends SET status = ? WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
            jdbcTemplate.update(updateSql, "CONFIRMED", userId, friendId, friendId, userId);
        }
    }

    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, friendId);

        if (rowsAffected == 0) {
            return;
        }

        String updateSql = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(updateSql, "UNCONFIRMED", friendId, userId);
    }

    public Collection<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.id = f1.friend_id " +
                "JOIN friends f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherUserId);
    }
}