package com.emdInc.emdbot.bot;


import com.emdInc.emdbot.bot.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {
    private static final Logger log = LoggerFactory.getLogger(BotInitializer.class);
    private final BotConfig botConfig;
    private final LongPollingBot bot;

    @Autowired
    public BotInitializer(BotConfig botConfig, LongPollingBot bot) {
        this.botConfig = botConfig;
        this.bot = bot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        if (botConfig.getToken() == null || botConfig.getBotName() == null) {
            log.error("Bot token or username is not configured.");
            throw new IllegalStateException("Bot token and username must be provided");
        }

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
            log.info("Bot registered successfully.");
        } catch (TelegramApiException e) {
            log.error("Could not initialize bot", e);
        }
    }
}
