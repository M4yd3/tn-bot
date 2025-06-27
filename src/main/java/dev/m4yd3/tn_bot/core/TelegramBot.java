package dev.m4yd3.tn_bot.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@RequiredArgsConstructor
@Component
@Slf4j
public class TelegramBot implements SpringLongPollingBot {
    private final TelegramClientWrapper client;
    private final UpdateConsumer commandHandler;

    @Override
    public String getBotToken() {
        return client.getConfig().getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return commandHandler;
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: {}", botSession.isRunning());

        client.setBotCommands();
    }
}
