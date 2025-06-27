package dev.m4yd3.tn_bot.service;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface RegistrationService {
    void handleRegistrationFlow(Message message);

    boolean isUserRegistered(Long telegramId);
}