package ru.yandex.practicum.filmorate.model.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

@Component
public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        String genresSql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.id";
        List<Genre> genres = jdbcTemplate.query(genresSql, (rs2, rowNum2) -> {
            Genre genre = new Genre();
            genre.setId(rs2.getLong("id"));
            genre.setName(rs2.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new HashSet<>(genres));

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(likesSql, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));

        return film;
    }
}
