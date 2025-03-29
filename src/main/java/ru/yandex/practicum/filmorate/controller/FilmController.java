package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        log.info("Получен список фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрошен фильм с id: {}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создан фильм " + film);
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновлен фильм " + film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавлен лайк фильму " + id + " от пользователя " + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удален лайк у фильма " + id + " от пользователя " + userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошены " + count + " популярных фильмов");
        return filmService.getPopularFilms(count);
    }
}
