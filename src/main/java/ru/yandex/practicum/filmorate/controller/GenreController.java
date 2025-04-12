package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        List<Genre> genres = (List<Genre>) genreStorage.getAllGenres();
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable long id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }
}