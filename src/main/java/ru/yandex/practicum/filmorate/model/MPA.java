package ru.yandex.practicum.filmorate.model;

import lombok.ToString;

@ToString
public enum MPA {
    G,
    PG,
    PG13,
    R,
    NC17;

    public String getNormalized() {
        return switch (this) {
            case G -> "G";
            case PG -> "PG";
            case PG13 -> "PG-13";
            case R -> "R";
            case NC17 -> "NC-17";
            default -> "Неизвестный рейтинг";
        };
    }
}
