package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id " + userId + " не найден"));
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
    }

    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        user.getFriends().remove(friendId);
    }

    public Collection<User> getFriends(long userId) {
        User user = getUserById(userId);
        return user.getFriends().keySet().stream()
                .map(this::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Long> userFriends = user.getFriends().keySet();
        Set<Long> otherUserFriends = otherUser.getFriends().keySet();

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(otherUserFriends);

        return commonFriendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
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