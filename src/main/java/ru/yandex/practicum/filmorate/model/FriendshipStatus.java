package ru.yandex.practicum.filmorate.model;

import lombok.ToString;

@ToString
public enum FriendshipStatus {
    UNCONFIRMED,
    CONFIRMED;

    public String getNormalized() {
        return switch (this) {
            case UNCONFIRMED -> "Неподтвержденная";
            case CONFIRMED -> "Подтвержденная";
        };
    }
}
