package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private FilmService filmService;

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
        Film addedFilm = filmController.addFilm(testFilm);

        assertNotNull(addedFilm.getId());
        assertEquals(testFilm.getName(), addedFilm.getName());
        assertEquals(testFilm.getDescription(), addedFilm.getDescription());
        assertEquals(testFilm.getReleaseDate(), addedFilm.getReleaseDate());
        assertEquals(testFilm.getDuration(), addedFilm.getDuration());
    }

    @Test
    void testUpdateFilm() {
        Film addedFilm = filmController.addFilm(testFilm);
        addedFilm.setName("Updated Film");

        Film updatedFilm = filmController.updateFilm(addedFilm);

        assertEquals("Updated Film", updatedFilm.getName());
    }

    @Test
    void testGetAllFilms() {
        filmController.addFilm(testFilm);

        Collection<Film> films = filmController.getAllFilms();

        assertThat(films).hasSize(1);
        assertThat(films).contains(testFilm);
    }

    @Test
    void testGetFilmById() {
        Film addedFilm = filmController.addFilm(testFilm);

        Film retrievedFilm = filmController.getFilmById(addedFilm.getId());

        assertEquals(addedFilm, retrievedFilm);
    }
}