package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        filmController.films.clear();
    }

    @Test
    void getAllFilms_ShouldReturnEmptyCollection_WhenNoFilmsAdded() {
        Collection<Film> films = filmController.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void getAllFilms_ShouldReturnAllFilms_WhenFilmsAdded() {
        Film film1 = new Film(1, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100);
        Film film2 = new Film(2, "Film 2", "Description 2", LocalDate.of(2010, 1, 1), 120);
        filmController.films.put(film1.getId(), film1);
        filmController.films.put(film2.getId(), film2);

        Collection<Film> films = filmController.getAllFilms();
        assertEquals(2, films.size());
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }

    @Test
    void createFilm_ShouldCreateFilm_WhenValidFilmProvided() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100);
        Film createdFilm = filmController.createFilm(film);
        assertNotNull(createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(1, filmController.films.size());
        assertTrue(filmController.films.containsValue(createdFilm));
    }

    @Test
    void createFilm_ShouldThrowValidationException_WhenReleaseDateIsBeforeThreshold() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(1895, 12, 27), 100);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertTrue(filmController.films.isEmpty());
    }

    @Test
    void createFilm_ShouldAssignCorrectId_WhenMultipleFilmsCreated() {
        Film film1 = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100);
        Film film2 = new Film(null, "Film 2", "Description 2", LocalDate.of(2000, 1, 1), 100);
        Film createdFilm1 = filmController.createFilm(film1);
        Film createdFilm2 = filmController.createFilm(film2);
        assertEquals(1, createdFilm1.getId());
        assertEquals(2, createdFilm2.getId());
    }

    @Test
    void updateFilm_ShouldUpdateFilm_WhenFilmExists() {
        Film film = new Film(1, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100);
        filmController.films.put(film.getId(), film);
        Film updatedFilm = new Film(1, "Updated Film", "Updated Description", LocalDate.of(2010, 1, 1), 120);
        Film result = filmController.updateFilm(updatedFilm);

        assertEquals(updatedFilm.getName(), result.getName());
        assertEquals(updatedFilm.getDescription(), result.getDescription());
        assertEquals(updatedFilm.getReleaseDate(), result.getReleaseDate());
        assertEquals(updatedFilm.getDuration(), result.getDuration());

        assertEquals(1, filmController.films.size());
        assertEquals(updatedFilm.getName(), filmController.films.get(1).getName());
    }

    @Test
    void updateFilm_ShouldThrowValidationException_WhenFilmDoesNotExist() {
        Film updatedFilm = new Film(1, "Updated Film", "Updated Description", LocalDate.of(2010, 1, 1), 120);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(updatedFilm));
    }
}