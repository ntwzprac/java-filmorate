package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

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
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashSet<>());
        userController.createUser(user1);
        userController.createUser(user2);

        Collection<User> users = userController.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void createUser_shouldCreateUserWithIdAndNameFromLogin_whenNameIsEmpty() {
        User user = new User(null, "test@example.com", "testLogin", "", LocalDate.of(2000, 1, 1), new HashSet<>());

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
        User user = new User(null, "test@example.com", "testLogin", null, LocalDate.of(2000, 1, 1), new HashSet<>());

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
        User user = new User(null, "test@example.com", "testLogin", "Test User", LocalDate.of(2000, 1, 1), new HashSet<>());

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
        User user = new User(null, "test@example.com", "testLogin", "Test User", LocalDate.of(2000, 1, 1), new HashSet<>());
        User createdUser = userController.createUser(user);
        User updatedUser = new User(createdUser.getId(), "updated@example.com", "updatedLogin", "Updated User", LocalDate.of(2001, 2, 2), new HashSet<>());

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
        User updatedUser = new User(1L, "new@example.com", "newLogin", "New User", LocalDate.of(2002, 3, 3), new HashSet<>());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(updatedUser));
        assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void createUser_ShouldAssignCorrectId_WhenMultipleUsersCreated() {
        User user1 = new User(null, "user1@example.com", "user1Login", "User 1", LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(null, "user2@example.com", "user2Login", "User 2", LocalDate.of(1995, 5, 5), new HashSet<>());
        User createdUser1 = userController.createUser(user1);
        User createdUser2 = userController.createUser(user2);
        assertEquals(1, createdUser1.getId());
        assertEquals(2, createdUser2.getId());
    }
}