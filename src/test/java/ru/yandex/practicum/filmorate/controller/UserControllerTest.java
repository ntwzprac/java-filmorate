package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        userController.users.clear();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersAdded() {
        Collection<User> users = userController.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers_whenUsersAdded() {
        User user1 = new User(null, "user1@example.com", "user1Login", null, LocalDate.of(1990, 1, 1));
        userController.createUser(user1);

        User user2 = new User(null, "user2@example.com", "user2Login", null, LocalDate.of(1995, 5, 5));
        userController.createUser(user2);

        Collection<User> users = userController.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void createUser_shouldCreateUserWithIdAndNameFromLogin_whenNameIsEmpty() {
        User user = new User(null,"test@example.com", "testLogin", "", LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("testLogin", createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
        assertTrue(userController.users.containsKey(createdUser.getId()));
    }

    @Test
    void createUser_shouldCreateUserWithIdAndNameFromLogin_whenNameIsNull() {
        User user = new User(null,"test@example.com", "testLogin", null, LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("testLogin", createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
        assertTrue(userController.users.containsKey(createdUser.getId()));
    }

    @Test
    void createUser_shouldCreateUserWithCorrectData() {
        User user = new User(null,"test@example.com", "testLogin", "Test User", LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
        assertTrue(userController.users.containsKey(createdUser.getId()));
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        User user = new User(null, "test@example.com", "testLogin", "Test User", LocalDate.of(2000, 1, 1));
        userController.createUser(user);
        int userId = user.getId();

        User updatedUser = new User(userId, "updated@example.com", "updatedLogin", "Updated User", LocalDate.of(2001, 2, 2));

        User resultUser = userController.updateUser(updatedUser);

        assertEquals(updatedUser.getId(), resultUser.getId());
        assertEquals(updatedUser.getEmail(), resultUser.getEmail());
        assertEquals(updatedUser.getLogin(), resultUser.getLogin());
        assertEquals(updatedUser.getName(), resultUser.getName());
        assertEquals(updatedUser.getBirthday(), resultUser.getBirthday());
        assertEquals(updatedUser.getEmail(), userController.users.get(userId).getEmail());
    }

    @Test
    void updateUser_shouldThrowValidationException_whenUserNotFound() {
        User newUser = new User(1, "new@example.com", "newLogin", "New User", LocalDate.of(2002, 3, 3));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(newUser));
        assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }
}