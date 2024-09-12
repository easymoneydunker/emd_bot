package com.emdInc.emdbot.bot;

import com.emdInc.emdbot.bot.config.BotConfig;
import com.emdInc.emdbot.entity.User;
import com.emdInc.emdbot.exception.NotFoundException;
import com.emdInc.emdbot.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    private final BotConfig botConfig;
    private final UserService userService;

    private final Map<Long, String> registrationStates = new HashMap<>();
    private final Map<Long, String> userInputs = new HashMap<>();
    private final Map<Long, String> pendingLogins = new HashMap<>();
    private final Map<Long, Boolean> loggedInUsers = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            log.info("Received message '{}' from chat ID {}", messageText, chatId);

            if (registrationStates.containsKey(chatId)) {
                log.info("Handling registration for chat ID {}", chatId);
                handleRegistration(chatId, messageText);
                return;
            }

            if (loggedInUsers.getOrDefault(chatId, false)) {
                if ("/logout".equals(messageText)) {
                    loggedInUsers.remove(chatId);
                    sendMessage(chatId, "You have been logged out.");
                    log.info("User logged out for chat ID {}", chatId);
                    return;
                }
                sendMessage(chatId, "Unknown command. Use /logout to log out.");
                return;
            }

            if (pendingLogins.containsKey(chatId)) {
                log.info("Handling login for chat ID {}", chatId);
                handleLogin(chatId, messageText);
                return;
            }

            switch (messageText) {
                case "/login":
                    sendMessage(chatId, "Please enter your username");
                    log.info("Login initiated for chat ID {}", chatId);
                    pendingLogins.put(chatId, "WAITING_FOR_PASSWORD");
                    break;
                case "/register":
                    sendMessage(chatId, "Please enter your username to register");
                    log.info("Registration initiated for chat ID {}", chatId);
                    registrationStates.put(chatId, "WAITING_FOR_USERNAME");
                    break;
                default:
                    sendMessage(chatId, "Unknown command. Use /register to start registration or /login to log in.");
                    break;
            }
        }
    }

    private void handleRegistration(Long chatId, String messageText) {
        String currentState = registrationStates.get(chatId);
        log.info("Handling registration state '{}' for chat ID {}", currentState, chatId);

        switch (currentState) {
            case "WAITING_FOR_USERNAME":
                userInputs.put(chatId, messageText);
                sendMessage(chatId, "Please enter your password");
                registrationStates.put(chatId, "WAITING_FOR_PASSWORD");
                log.info("Username received for chat ID {}. Awaiting password.", chatId);
                break;
            case "WAITING_FOR_PASSWORD":
                String username = userInputs.get(chatId);
                String password = messageText;

                User user = new User();
                user.setLogin(username);
                user.setPassword(password);
                user.setEmail(username + "@example.com");

                try {
                    userService.saveUser(user);
                    sendMessage(chatId, "Registration successful! You can now log in.");
                    log.info("User registered successfully: {}", username);
                } catch (Exception e) {
                    log.error("Registration error for chat ID {}: ", chatId, e);
                    sendMessage(chatId, "Registration failed. Please try again.");
                }

                registrationStates.remove(chatId);
                userInputs.remove(chatId);
                break;
        }
    }

    private void handleLogin(Long chatId, String messageText) {
        String currentState = pendingLogins.get(chatId);
        log.info("Handling login state '{}' for chat ID {}", currentState, chatId);

        if ("WAITING_FOR_PASSWORD".equals(currentState)) {
            String username = messageText;
            pendingLogins.put(chatId, username);
            sendMessage(chatId, "Please enter your password");
            log.info("Username received for login from chat ID {}. Awaiting password.", chatId);
            return;
        }

        String password = messageText;
        String username = pendingLogins.get(chatId);

        try {
            User user = userService.findByLogin(username);
            if (user.getPassword().equals(password)) {
                loggedInUsers.put(chatId, true);
                sendMessage(chatId, "Login successful! Use /logout to log out.");
                log.info("User logged in successfully: {}", username);
            } else {
                sendMessage(chatId, "Login failed. Incorrect password.");
                log.warn("Login failed due to incorrect password for username: {}", username);
            }
        } catch (NotFoundException e) {
            sendMessage(chatId, "Login failed. User not found.");
            log.warn("Login failed. User not found for username: {}", username);
        }

        pendingLogins.remove(chatId);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
            log.info("Sent message to chat ID {}: {}", chatId, textToSend);
        } catch (TelegramApiException e) {
            log.error("Bot could not send a message to chat ID {}: ", chatId, e);
        }
    }
}
