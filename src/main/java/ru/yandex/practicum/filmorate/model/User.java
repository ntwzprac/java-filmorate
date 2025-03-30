package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Некорректный не может быть пустым")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем")
    private LocalDate birthday;
    private HashMap<Long, FriendshipStatus> friends = new HashMap<>();
}