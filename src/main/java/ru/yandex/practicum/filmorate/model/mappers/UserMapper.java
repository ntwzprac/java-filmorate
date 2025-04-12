package ru.yandex.practicum.filmorate.model.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@Component
public class UserMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        String friendsSql = "SELECT friend_id, status FROM friends WHERE user_id = ?";
        HashMap<Long, FriendshipStatus> friends = new HashMap<>();
        jdbcTemplate.query(friendsSql, (rs2, rowNum2) -> {
            friends.put(rs2.getLong("friend_id"),
                    rs2.getString("status").equals("CONFIRMED") ? FriendshipStatus.CONFIRMED : FriendshipStatus.UNCONFIRMED);
            return null;
        }, user.getId());
        user.setFriends(friends);

        return user;
    }
}