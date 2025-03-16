package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    Optional<User> getUserById(long userId);

    Collection<User> getAllUsers();
}
