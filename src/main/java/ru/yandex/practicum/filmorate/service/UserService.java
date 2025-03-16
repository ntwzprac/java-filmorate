package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new ValidationException("Пользователь с id " + friendId + " не найден"));
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new ValidationException("Пользователь с id " + friendId + " не найден"));
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден"));
        User otherUser = userStorage.getUserById(otherUserId).orElseThrow(() -> new ValidationException("Пользователь с id " + otherUserId + " не найден"));

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(otherUserFriends);

        return commonFriendsIds.stream()
                .map(id -> userStorage.getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getUserFriends(long userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден"));
        return user.getFriends().stream()
                .map(id -> userStorage.getUserById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}