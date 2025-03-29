package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long filmId);

    Optional<Film> getFilmById(long filmId);

    Collection<Film> getAllFilms();
}
