package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersAdded() {
        Collection<User> users = userController.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers_whenUsersAdded() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void createUser_shouldCreateUserWithIdAndNameFromLogin_whenNameIsEmpty() {
        User user = new User(null, "test@example.com", "testLogin", "", LocalDate.of(2000, 1, 1), new HashMap<>());

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("testLogin", createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
        assertEquals(1, userStorage.getAllUsers().size());
        assertTrue(userStorage.getAllUsers().contains(createdUser));
    }

    @Test
    void createUser_shouldCreateUserWithIdAndNameFromLogin_whenNameIsNull() {
        User user = new User(null, "test@example.com", "testLogin", null, LocalDate.of(2000, 1, 1), new HashMap<>());

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("testLogin", createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
        assertEquals(1, userStorage.getAllUsers().size());
        assertTrue(userStorage.getAllUsers().contains(createdUser));
    }

    @Test
    void createUser_shouldCreateUserWithCorrectData() {
        User user = new User(null, "test@example.com", "testLogin", "Test User", LocalDate.of(2000, 1, 1), new HashMap<>());

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
        assertEquals(1, userStorage.getAllUsers().size());
        assertTrue(userStorage.getAllUsers().contains(createdUser));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        User user = new User(null, "test@example.com", "testLogin", "Test User", LocalDate.of(2000, 1, 1), new HashMap<>());
        User createdUser = userController.createUser(user);
        User updatedUser = new User(createdUser.getId(), "updated@example.com", "updatedLogin", "Updated User", LocalDate.of(2001, 2, 2), new HashMap<>());

        User resultUser = userController.updateUser(updatedUser);

        assertEquals(updatedUser.getId(), resultUser.getId());
        assertEquals(updatedUser.getEmail(), resultUser.getEmail());
        assertEquals(updatedUser.getLogin(), resultUser.getLogin());
        assertEquals(updatedUser.getName(), resultUser.getName());
        assertEquals(updatedUser.getBirthday(), resultUser.getBirthday());

        assertEquals(1, userStorage.getAllUsers().size());
        Optional<User> resultFromStorage = userStorage.getUserById(createdUser.getId());
        assertTrue(resultFromStorage.isPresent());
        assertEquals(updatedUser.getName(), resultFromStorage.get().getName());
    }

    @Test
    void updateUser_shouldThrowValidationException_whenUserNotFound() {
        User updatedUser = new User(1L, "new@example.com", "newLogin", "New User", LocalDate.of(2002, 3, 3), new HashMap<>());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(updatedUser));
        assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void createUser_ShouldAssignCorrectId_WhenMultipleUsersCreated() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);
        assertEquals(1, createdUser1.getId());
        assertEquals(2, createdUser2.getId());
    }

    @Test
    void addFriend_shouldAddFriend() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);

        userController.addFriend(createdUser1.getId(), createdUser2.getId());

        assertEquals(FriendshipStatus.UNCONFIRMED, userStorage.getUserById(createdUser1.getId()).get().getFriends().get(createdUser2.getId()));
        assertEquals(FriendshipStatus.UNCONFIRMED, userStorage.getUserById(createdUser2.getId()).get().getFriends().get(createdUser1.getId()));
    }

    @Test
    void removeFriend_shouldRemoveFriend() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);
        userController.addFriend(createdUser1.getId(), createdUser2.getId());

        userController.deleteFriend(createdUser1.getId(), createdUser2.getId());

        assertFalse(userStorage.getUserById(createdUser1.getId()).get().getFriends().containsKey(createdUser2.getId()));
        assertFalse(userStorage.getUserById(createdUser2.getId()).get().getFriends().containsKey(createdUser1.getId()));
    }

    @Test
    void getFriends_shouldReturnFriendsList() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User user3 = new User(null, "user3@example.com", "user3Login", "User 3", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);
        User createdUser3 = userController.createUser(user3);
        userController.addFriend(createdUser1.getId(), createdUser2.getId());
        userController.addFriend(createdUser1.getId(), createdUser3.getId());

        Collection<User> friends = userController.getFriends(createdUser1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.contains(createdUser2));
        assertTrue(friends.contains(createdUser3));
    }

    @Test
    void getCommonFriends_shouldReturnCommonFriendsList() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User user3 = new User(null, "user3@example.com", "user3Login", "User 3", LocalDate.of(1995, 5, 5), new HashMap<>());
        User user4 = new User(null, "user4@example.com", "user4Login", "User 4", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);
        User createdUser3 = userController.createUser(user3);
        User createdUser4 = userController.createUser(user4);
        userController.addFriend(createdUser1.getId(), createdUser3.getId());
        userController.addFriend(createdUser2.getId(), createdUser3.getId());
        userController.addFriend(createdUser1.getId(), createdUser4.getId());

        Collection<User> commonFriends = userController.getCommonFriends(createdUser1.getId(), createdUser2.getId());

        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(createdUser3));
    }

    @Test
    void getFriends_shouldReturnEmptyList_whenNoFriends() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User createdUser1 = userController.createUser(user1);

        Collection<User> friends = userController.getFriends(createdUser1.getId());

        assertTrue(friends.isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnEmptyList_whenNoCommonFriends() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);

        Collection<User> commonFriends = userController.getCommonFriends(createdUser1.getId(), createdUser2.getId());

        assertTrue(commonFriends.isEmpty());
    }
    @Test
    void addFriend_shouldThrowNotFoundException_whenUserNotFound() {
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser2 = userController.createUser(user2);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.addFriend(999L, createdUser2.getId()));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void addFriend_shouldThrowNotFoundException_whenFriendNotFound() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User createdUser1 = userController.createUser(user1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.addFriend(createdUser1.getId(), 999L));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
    @Test
    void deleteFriend_shouldThrowNotFoundException_whenUserNotFound() {
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser2 = userController.createUser(user2);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.deleteFriend(999L, createdUser2.getId()));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }

    @Test
    void deleteFriend_shouldThrowNotFoundException_whenFriendNotFound() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User createdUser1 = userController.createUser(user1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.deleteFriend(createdUser1.getId(), 999L));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
    @Test
    void getFriends_shouldThrowNotFoundException_whenUserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getFriends(999L));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
    @Test
    void getCommonFriends_shouldThrowNotFoundException_whenUserNotFound() {
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashMap<>());
        User createdUser2 = userController.createUser(user2);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getCommonFriends(999L, createdUser2.getId()));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
    @Test
    void getCommonFriends_shouldThrowNotFoundException_whenOtherUserNotFound() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashMap<>());
        User createdUser1 = userController.createUser(user1);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getCommonFriends(createdUser1.getId(), 999L));
        assertEquals("Пользователь с id 999 не найден", exception.getMessage());
    }
}