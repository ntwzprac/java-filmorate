package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ContextConfiguration(classes = {UserDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

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
        User addedUser = userStorage.addUser(testUser);

        assertThat(addedUser.getId()).isNotNull();
        assertThat(addedUser.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(addedUser.getLogin()).isEqualTo(testUser.getLogin());
        assertThat(addedUser.getName()).isEqualTo(testUser.getName());
        assertThat(addedUser.getBirthday()).isEqualTo(testUser.getBirthday());
    }

    @Test
    void testUpdateUser() {
        User addedUser = userStorage.addUser(testUser);
        addedUser.setName("Updated User");

        User updatedUser = userStorage.updateUser(addedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated User");
    }

    @Test
    void testGetAllUsers() {
        userStorage.addUser(testUser);

        assertThat(userStorage.getAllUsers()).hasSize(1);
    }

    @Test
    void testGetUserById() {
        User addedUser = userStorage.addUser(testUser);

        Optional<User> retrievedUser = userStorage.getUserById(addedUser.getId());

        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).isEqualTo(addedUser)
                );
    }

    @Test
    void testDeleteUser() {
        User addedUser = userStorage.addUser(testUser);
        userStorage.deleteUser(addedUser.getId());

        assertThat(userStorage.getUserById(addedUser.getId())).isEmpty();
    }
}