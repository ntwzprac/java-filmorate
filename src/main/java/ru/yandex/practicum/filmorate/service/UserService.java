package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public void deleteUser(long userId) {
        getUserById(userId);
        userStorage.deleteUser(userId);
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        getUserById(userId);
        getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<User> getUserFriends(long userId) {
        User user = getUserById(userId);
        Set<Long> confirmedFriends = new HashSet<>();

        for (Map.Entry<Long, FriendshipStatus> entry : user.getFriends().entrySet()) {
            Long friendId = entry.getKey();
            User friend = getUserById(friendId);

            if (friend.getFriends().containsKey(userId)) {
                confirmedFriends.add(friendId);
            }
        }

        return confirmedFriends.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}