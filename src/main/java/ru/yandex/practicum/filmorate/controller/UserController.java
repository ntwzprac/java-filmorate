package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен список пользователей");
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрошен пользователь с id: {}", id);
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user.get();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {

        validateAndSetName(user);
        log.info("Создан пользователь " + user);
        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateAndSetName(user);
        log.info("Обновлен пользователь " + user);
        try {
            return userStorage.updateUser(user);
        } catch (RuntimeException e) {
            log.info("Не удалось обновить пользователя " + user + ". Пользователь не найден.");
            throw new NotFoundException(e.getMessage());
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь " + id + " добавляет в друзья пользователя " + friendId);
        Optional<User> userOptional = userStorage.getUserById(id);
        Optional<User> friendOptional = userStorage.getUserById(friendId);
        if (userOptional.isEmpty() || friendOptional.isEmpty()) {
            throw new NotFoundException("Не найден один из пользователей");
        }
        User user = userOptional.get();
        User friend = friendOptional.get();
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь " + id + " удаляет из друзей пользователя " + friendId);

        Optional<User> userOptional = userStorage.getUserById(id);
        Optional<User> friendOptional = userStorage.getUserById(friendId);

        if (userOptional.isEmpty() || friendOptional.isEmpty()) {
            throw new NotFoundException("Не найден один из пользователей");
        }

        User user = userOptional.get();
        User friend = friendOptional.get();

        boolean userFriendRemoved = user.getFriends().remove(friendId);
        boolean friendFriendRemoved = friend.getFriends().remove(id);

        if (!userFriendRemoved && !friendFriendRemoved) {
            log.info("Пользователь {} не был в друзьях у {}", friendId, id);
        }
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Запрошен список друзей пользователя " + id);
        Optional<User> userOptional = userStorage.getUserById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Не найден пользователь");
        }
        User user = userOptional.get();
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрошен список общих друзей пользователя " + id + " и " + otherId);
        Optional<User> userOptional = userStorage.getUserById(id);
        Optional<User> otherUserOptional = userStorage.getUserById(otherId);
        if (userOptional.isEmpty() || otherUserOptional.isEmpty()) {
            throw new NotFoundException("Не найден один из пользователей");
        }
        User user = userOptional.get();
        User otherUser = otherUserOptional.get();
        Set<Long> commonFriendsId = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());
        return commonFriendsId.stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void validateAndSetName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}