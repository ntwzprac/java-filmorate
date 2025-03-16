package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        log.info("Получен список фильмов");
        return filmStorage.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрошен фильм с id: {}", id);
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return film.get();
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        log.info("Создан фильм " + film);
        return filmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        log.info("Обновлен фильм " + film);
        try {
            return filmStorage.updateFilm(film);
        } catch (RuntimeException e) {
            log.info("Не удалось обновить фильм " + film + ". Фильм не найден.");
            throw new NotFoundException(e.getMessage());
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавлен лайк фильму " + id + " от пользователя " + userId);
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        Optional<User> userOptional = userStorage.getUserById(userId);
        if (filmOptional.isEmpty() || userOptional.isEmpty()) {
            throw new NotFoundException("Фильм или пользователь не найден");
        }
        Film film = filmOptional.get();
        film.getLikes().add(userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удален лайк у фильма " + id + " от пользователя " + userId);
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        if (filmOptional.isEmpty()) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = filmOptional.get();
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден");
        }
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошены " + count + " популярных фильмов");
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза фильма " + film.getName() + " раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }
}
