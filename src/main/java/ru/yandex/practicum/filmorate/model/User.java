package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    Integer id;

    @NotEmpty(message = "email не может быть пустым")
    @Email(message = "email должен быть валидным")
    String email;

    @Pattern(regexp = "^\\S+$", message = "login не может содержать пробелы")
    @NotEmpty(message = "login не может быть пустым")
    String login;

    String name;

    @Past(message = "birthday не может быть в будущем")
    @NotNull(message = "birthday не может быть пустым")
    LocalDate birthday;
}