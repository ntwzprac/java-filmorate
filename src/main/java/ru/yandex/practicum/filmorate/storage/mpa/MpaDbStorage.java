package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> getMpaById(long id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id)
                .stream()
                .findFirst();
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}