package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    public HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен список пользователей");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(getNextId());

        validateAndSetName(user);

        users.put(user.getId(), user);
        log.info("Создан пользователь " + user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            User user = users.get(newUser.getId());
            user.setName(newUser.getName());
            user.setEmail(newUser.getEmail());
            user.setLogin(newUser.getLogin());
            user.setBirthday(newUser.getBirthday());
            log.info("Обновлен пользователь " + user);
            return user;
        } else {
            log.info("Не удалось обновить пользователя " + newUser + ". Пользователь не найден.");
            throw new ValidationException("Пользователь с id " + newUser.getId() + " не найден");
        }
    }

    private @NotNull int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateAndSetName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}