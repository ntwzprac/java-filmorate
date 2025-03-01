package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    public Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен список фильмов");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        checkValidDate(film);

        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Создан фильм " + film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            checkValidDate(newFilm);

            Film film = films.get(newFilm.getId());
            film.setName(newFilm.getName());
            film.setDescription(newFilm.getDescription());
            film.setReleaseDate(newFilm.getReleaseDate());
            film.setDuration(newFilm.getDuration());
            log.info("Обновлен фильм " + film);
            return film;
        } else {
            log.info("Не удалось обновить фильм " + newFilm + ". Фильм не найден.");
            throw new ValidationException("Фильм с id " + newFilm.getId() + " не найден");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkValidDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
