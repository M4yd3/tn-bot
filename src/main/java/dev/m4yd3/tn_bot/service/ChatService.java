package dev.m4yd3.tn_bot.service;

import dev.m4yd3.tn_bot.db.entity.Chat;

public interface ChatService {
    Chat getOrCreateChatFromTelegram(org.telegram.telegrambots.meta.api.objects.chat.Chat telegramChat);

    Chat getOrCreateChatFromTelegram(Long telegramChatId);

    Chat getOrCreateChatFromTelegramWithAdmin(
            org.telegram.telegrambots.meta.api.objects.chat.Chat telegramChat,
            boolean isAdmin
    );
}
