package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {
    Integer id;

    @NotEmpty(message = "name не может быть пустым")
    String name;

    @Length(max = 200, message = "description не может быть длиннее 200 символов")
    String description;

    @NotNull
    LocalDate releaseDate;

    @NotNull
    @Positive(message = "duration не может быть отрицательным")
    Integer duration;
}
