package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable long id) {
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}