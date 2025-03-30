package ru.yandex.practicum.filmorate.model;

import lombok.ToString;

@ToString
public enum Genre {
    COMEDY,
    DRAMA,
    CARTOON,
    THRILLER,
    DOCUMENTARY,
    ACTION;

    public String getNormalized() {
        return switch (this) {
            case COMEDY -> "Комедия";
            case DRAMA -> "Драма";
            case CARTOON -> "Мультфильм";
            case THRILLER -> "Триллер";
            case DOCUMENTARY -> "Документальный";
            case ACTION -> "Боевик";
            default -> "Неизвестный жанр";
        };
    }
}
