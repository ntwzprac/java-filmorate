package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testlogin");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testAddUser() {
        User addedUser = userController.addUser(testUser);

        assertNotNull(addedUser.getId());
        assertEquals(testUser.getEmail(), addedUser.getEmail());
        assertEquals(testUser.getLogin(), addedUser.getLogin());
        assertEquals(testUser.getName(), addedUser.getName());
        assertEquals(testUser.getBirthday(), addedUser.getBirthday());
    }

    @Test
    void testUpdateUser() {
        User addedUser = userController.addUser(testUser);
        addedUser.setName("Updated User");

        User updatedUser = userController.updateUser(addedUser);

        assertEquals("Updated User", updatedUser.getName());
    }

    @Test
    void testGetAllUsers() {
        userController.addUser(testUser);

        Collection<User> users = userController.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users).contains(testUser);
    }

    @Test
    void testGetUserById() {
        User addedUser = userController.addUser(testUser);

        User retrievedUser = userController.getUserById(addedUser.getId());

        assertEquals(addedUser, retrievedUser);
    }
}