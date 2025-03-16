package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmAndUser(filmId, userId);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmAndUser(filmId, userId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк от пользователя " + userId + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        Collection<Film> allFilms = filmStorage.getAllFilms();
        return allFilms.stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmAndUser(long filmId, long userId) {
        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);
        Optional<User> userOptional = userStorage.getUserById(userId);

        if (filmOptional.isEmpty() || userOptional.isEmpty()) {
            if (filmOptional.isEmpty()) {
                throw new NotFoundException("Фильм с id " + filmId + " не найден");
            } else {
                throw new NotFoundException("Пользователь с id " + userId + " не найден");
            }
        }
        return filmOptional.get();
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        try {
            return filmStorage.updateFilm(film);
        } catch (RuntimeException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }
}