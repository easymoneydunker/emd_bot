package com.emdInc.emdbot.bot.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().filename("env/.env").load();

        String botToken = dotenv.get("BOT_TOKEN");
        System.out.println("Bot Token: " + botToken);
    }
}



