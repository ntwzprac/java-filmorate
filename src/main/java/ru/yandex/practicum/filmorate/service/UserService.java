package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long userId) {
        return checkAndGetUserById(userId);
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = checkAndGetUserById(userId);
        User friend = checkAndGetUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(long userId, long friendId) {
        User user = checkAndGetUserById(userId);
        User friend = checkAndGetUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = checkAndGetUserById(userId);
        User otherUser = checkAndGetUserById(otherUserId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(otherUserFriends);

        return commonFriendsIds.stream()
                .map(this::checkAndGetUserById)
                .collect(Collectors.toList());
    }

    public List<User> getUserFriends(long userId) {
        User user = checkAndGetUserById(userId);
        return user.getFriends().stream()
                .map(this::checkAndGetUserById)
                .collect(Collectors.toList());
    }

    private User checkAndGetUserById(long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}