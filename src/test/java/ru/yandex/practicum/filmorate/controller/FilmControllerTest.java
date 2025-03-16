package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    private FilmController filmController;
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmController = new FilmController(filmStorage, userStorage);
    }

    @Test
    void getAllFilms_ShouldReturnEmptyCollection_WhenNoFilmsAdded() {
        Collection<Film> films = filmController.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void getAllFilms_ShouldReturnAllFilms_WhenFilmsAdded() {
        Film film1 = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film film2 = new Film(null, "Film 2", "Description 2", LocalDate.of(2010, 1, 1), 120, new HashSet<>());
        filmController.createFilm(film1);
        filmController.createFilm(film2);

        Collection<Film> films = filmController.getAllFilms();
        assertEquals(2, films.size());
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }

    @Test
    void createFilm_ShouldCreateFilm_WhenValidFilmProvided() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film createdFilm = filmController.createFilm(film);
        assertNotNull(createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(1, filmStorage.getAllFilms().size());
        assertTrue(filmStorage.getAllFilms().contains(createdFilm));
    }

    @Test
    void createFilm_ShouldThrowValidationException_WhenReleaseDateIsBeforeThreshold() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(1895, 12, 27), 100, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertTrue(filmStorage.getAllFilms().isEmpty());
    }

    @Test
    void createFilm_ShouldAssignCorrectId_WhenMultipleFilmsCreated() {
        Film film1 = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film film2 = new Film(null, "Film 2", "Description 2", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film createdFilm1 = filmController.createFilm(film1);
        Film createdFilm2 = filmController.createFilm(film2);
        assertEquals(1, createdFilm1.getId());
        assertEquals(2, createdFilm2.getId());
    }

    @Test
    void updateFilm_ShouldUpdateFilm_WhenFilmExists() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film createdFilm = filmController.createFilm(film);
        Film updatedFilm = new Film(createdFilm.getId(), "Updated Film", "Updated Description", LocalDate.of(2010, 1, 1), 120, new HashSet<>());
        Film result = filmController.updateFilm(updatedFilm);

        assertEquals(updatedFilm.getName(), result.getName());
        assertEquals(updatedFilm.getDescription(), result.getDescription());
        assertEquals(updatedFilm.getReleaseDate(), result.getReleaseDate());
        assertEquals(updatedFilm.getDuration(), result.getDuration());

        assertEquals(1, filmStorage.getAllFilms().size());
        Optional<Film> resultFromStorage = filmStorage.getFilmById(createdFilm.getId());
        assertTrue(resultFromStorage.isPresent());
        assertEquals(updatedFilm.getName(), resultFromStorage.get().getName());
    }

    @Test
    void updateFilm_ShouldThrowValidationException_WhenFilmDoesNotExist() {
        Film updatedFilm = new Film(1L, "Updated Film", "Updated Description", LocalDate.of(2010, 1, 1), 120, new HashSet<>());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.updateFilm(updatedFilm));
        assertEquals("Фильм с id 1 не найден", ex.getMessage());
    }
}