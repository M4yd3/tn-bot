package dev.m4yd3.tn_bot.service;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.db.entity.User;

import java.util.List;

public interface UserService {
    User getOrCreateUserFromTelegram(org.telegram.telegrambots.meta.api.objects.User telegramUser);

    List<User> getOrCreateUsersFromTelegram(List<org.telegram.telegrambots.meta.api.objects.User> telegramUsers);

    void saveUserToChat(User user, Chat chat);

    void saveUsersToChat(List<User> users, Chat chat);

    void deleteUserFromChat(User user, Chat chat);

    boolean isUserExcludedInChat(User user, Chat chat);

    void toggleIsExcluded(User user, Chat chat);

    boolean shouldDeleteMessageFromUserInChat(Long userId, Long chatId);
}
