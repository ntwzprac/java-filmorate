package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.mappers.MpaMapper;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, mpaMapper);
    }

    @Override
    public Optional<Mpa> getMpaById(long id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sql, mpaMapper, id)
                .stream()
                .findFirst();
    }
}