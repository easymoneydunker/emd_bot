package com.emdInc.emdbot;

import com.emdInc.emdbot.entity.User;
import com.emdInc.emdbot.repository.UserRepository;
import com.emdInc.emdbot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setLogin("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
    }

    @Test
    void saveUser_ShouldSaveAndReturnUser() {
        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser.getId());
        assertEquals(user.getLogin(), savedUser.getLogin());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User savedUser = userService.saveUser(user);

        User foundUser = userService.findById(savedUser.getId());

        assertEquals(savedUser.getLogin(), foundUser.getLogin());
    }

    @Test
    void findByLogin_ShouldReturnUser_WhenUserExists() {
        userService.saveUser(user);

        User foundUser = userService.findByLogin("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getLogin());
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        User savedUser = userService.saveUser(user);

        userService.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertTrue(deletedUser.isEmpty());
    }
}
