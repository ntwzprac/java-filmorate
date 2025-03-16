package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
        filmStorage.getFilmById(filmId).orElseThrow(() -> new ValidationException("Фильм с id " + filmId + " не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден"));
        Film film = filmStorage.getFilmById(filmId).get();
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).orElseThrow(() -> new ValidationException("Фильм с id " + filmId + " не найден"));
        userStorage.getUserById(userId).orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден"));
        Film film = filmStorage.getFilmById(filmId).get();
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
}