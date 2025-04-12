package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {FilmDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        testFilm.setMpa(mpa);
    }

    @Test
    void testAddFilm() {
        Film addedFilm = filmStorage.addFilm(testFilm);

        assertThat(addedFilm.getId()).isNotNull();
        assertThat(addedFilm.getName()).isEqualTo(testFilm.getName());
        assertThat(addedFilm.getDescription()).isEqualTo(testFilm.getDescription());
        assertThat(addedFilm.getReleaseDate()).isEqualTo(testFilm.getReleaseDate());
        assertThat(addedFilm.getDuration()).isEqualTo(testFilm.getDuration());
    }

    @Test
    void testUpdateFilm() {
        Film addedFilm = filmStorage.addFilm(testFilm);
        addedFilm.setName("Updated Film");

        Film updatedFilm = filmStorage.updateFilm(addedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
    }

    @Test
    void testGetAllFilms() {
        filmStorage.addFilm(testFilm);

        assertThat(filmStorage.getAllFilms()).hasSize(1);
    }

    @Test
    void testGetFilmById() {
        Film addedFilm = filmStorage.addFilm(testFilm);

        Optional<Film> retrievedFilm = filmStorage.getFilmById(addedFilm.getId());

        assertThat(retrievedFilm)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).isEqualTo(addedFilm)
                );
    }

    @Test
    void testDeleteFilm() {
        Film addedFilm = filmStorage.addFilm(testFilm);
        filmStorage.deleteFilm(addedFilm.getId());

        assertThat(filmStorage.getFilmById(addedFilm.getId())).isEmpty();
    }
}