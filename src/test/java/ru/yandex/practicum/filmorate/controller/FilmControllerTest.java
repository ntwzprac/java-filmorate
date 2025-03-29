package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
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
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
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
    void updateFilm_ShouldThrowNotFoundException_WhenFilmDoesNotExist() {
        Film updatedFilm = new Film(1L, "Updated Film", "Updated Description", LocalDate.of(2010, 1, 1), 120, new HashSet<>());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.updateFilm(updatedFilm));
        assertEquals("Фильм с id 1 не найден", ex.getMessage());
    }

    @Test
    void getFilmById_ShouldReturnFilm_WhenFilmExists() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film createdFilm = filmController.createFilm(film);
        Film retrievedFilm = filmController.getFilmById(createdFilm.getId());

        assertEquals(createdFilm.getId(), retrievedFilm.getId());
        assertEquals(createdFilm.getName(), retrievedFilm.getName());
    }

    @Test
    void getFilmById_ShouldThrowNotFoundException_WhenFilmDoesNotExist() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.getFilmById(1L));
        assertEquals("Фильм с id 1 не найден", ex.getMessage());
    }

    @Test
    void addLike_ShouldAddLikeToFilm_WhenFilmAndUserExist() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        User user = new User(null, "user@email.com", "userLogin", "userName", LocalDate.of(1990, 1, 1), new HashSet<>());
        filmController.createFilm(film);
        userStorage.addUser(user);
        filmController.addLike(1L, 1L);

        assertEquals(1, filmStorage.getFilmById(1L).get().getLikes().size());
        assertTrue(filmStorage.getFilmById(1L).get().getLikes().contains(1L));
    }

    @Test
    void addLike_ShouldThrowNotFoundException_WhenFilmDoesNotExist() {
        User user = new User(null, "user@email.com", "userLogin", "userName", LocalDate.of(1990, 1, 1), new HashSet<>());
        userStorage.addUser(user);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.addLike(1L, 1L));
        assertEquals("Фильм с id 1 не найден", ex.getMessage());
    }

    @Test
    void addLike_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        filmController.createFilm(film);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.addLike(1L, 1L));
        assertEquals("Пользователь с id 1 не найден", ex.getMessage());
    }

    @Test
    void deleteLike_ShouldDeleteLikeFromFilm_WhenLikeExists() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        User user = new User(null, "user@email.com", "userLogin", "userName", LocalDate.of(1990, 1, 1), new HashSet<>());
        filmController.createFilm(film);
        userStorage.addUser(user);
        filmController.addLike(1L, 1L);
        filmController.deleteLike(1L, 1L);

        assertEquals(0, filmStorage.getFilmById(1L).get().getLikes().size());
        assertFalse(filmStorage.getFilmById(1L).get().getLikes().contains(1L));
    }

    @Test
    void deleteLike_ShouldThrowNotFoundException_WhenLikeDoesNotExist() {
        Film film = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        User user = new User(null, "user@email.com", "userLogin", "userName", LocalDate.of(1990, 1, 1), new HashSet<>());
        filmController.createFilm(film);
        userStorage.addUser(user);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.deleteLike(1L, 1L));
        assertEquals("Лайк от пользователя 1 не найден", ex.getMessage());
    }

    @Test
    void getPopularFilms_ShouldReturnSortedFilms_WhenFilmsHaveLikes() {
        Film film1 = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film film2 = new Film(null, "Film 2", "Description 2", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        User user1 = new User(null, "user1@email.com", "userLogin1", "userName1", LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(null, "user2@email.com", "userLogin2", "userName2", LocalDate.of(1990, 1, 1), new HashSet<>());
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmController.addLike(1L, 1L);
        filmController.addLike(1L, 2L);
        filmController.addLike(2L, 1L);

        assertEquals(2, filmController.getPopularFilms(10).size());
        assertEquals(1, filmController.getPopularFilms(10).get(0).getId());
        assertEquals(2, filmController.getPopularFilms(10).get(1).getId());
    }

    @Test
    void getPopularFilms_ShouldReturnCorrectAmount_WhenCountIsSpecified() {
        Film film1 = new Film(null, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film film2 = new Film(null, "Film 2", "Description 2", LocalDate.of(2000, 1, 1), 100, new HashSet<>());
        Film film3 = new Film(null, "Film 3", "Description 3", LocalDate.of(2000, 1, 1), 100, new HashSet<>());

        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film3);

        assertEquals(3, filmController.getPopularFilms(10).size());
        assertEquals(2, filmController.getPopularFilms(2).size());
    }
}