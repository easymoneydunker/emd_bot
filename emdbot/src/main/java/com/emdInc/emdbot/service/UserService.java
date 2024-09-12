package com.emdInc.emdbot.service;

import com.emdInc.emdbot.entity.User;
import com.emdInc.emdbot.exception.NotFoundException;
import com.emdInc.emdbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        logger.info("Saving user with login: {}", user.getLogin());
        return userRepository.save(user);
    }

    public User findById(Long id) {
        logger.info("Finding user by ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID {} not found", id);
            return new NotFoundException("User with ID " + id + " not found.");
        });
    }

    public User findByLogin(String login) {
        logger.info("Finding user by login: {}", login);
        return userRepository.findByLogin(login).orElseThrow(() -> {
            logger.error("User with login {} not found", login);
            return new NotFoundException("User with login " + login + " not found.");
        });
    }

    public User findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("User with email {} not found", email);
            return new NotFoundException("User with email " + email + " not found.");
        });
    }

    public List<User> findAll() {
        logger.info("Retrieving list of all users");
        return userRepository.findAll();
    }

    public void deleteById(Long id) {
        logger.info("Deleting user with ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("User with ID {} successfully deleted", id);
        } else {
            logger.error("User with ID {} not found for deletion", id);
            throw new NotFoundException("User with ID " + id + " not found for deletion.");
        }
    }
}
