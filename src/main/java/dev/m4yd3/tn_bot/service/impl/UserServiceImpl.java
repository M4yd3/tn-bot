package dev.m4yd3.tn_bot.service.impl;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.db.entity.User;
import dev.m4yd3.tn_bot.db.repository.UserRepository;
import dev.m4yd3.tn_bot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getOrCreateUserFromTelegram(final org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        return userRepository.findByTelegramId(telegramUser.getId())
                .orElseGet(() -> userRepository.save(new User(telegramUser.getId(), telegramUser.getUserName())));
    }

    @Override
    public List<User> getOrCreateUsersFromTelegram(final List<org.telegram.telegrambots.meta.api.objects.User> telegramUsers) {
        return userRepository.findAllByTelegramIdIn(telegramUsers.stream()
                .map(org.telegram.telegrambots.meta.api.objects.User::getId)
                .toList());
    }

    @Override
    public void saveUserToChat(final User user, final Chat chat) {
        final var sql = "insert into users_chats(user_id, chat_id) values (?, ?) on conflict do nothing";
        jdbcTemplate.update(sql, user.getId(), chat.getId());
    }

    @Override
    public void saveUsersToChat(final List<User> users, final Chat chat) {
        final var sql = "insert into users_chats(user_id, chat_id) values (?, ?) on conflict do nothing";
        final var args = users.stream().map(u -> new Object[]{u.getId(), chat.getId()}).toList();
        jdbcTemplate.batchUpdate(sql, args);
    }

    @Override
    public void deleteUserFromChat(final User user, final Chat chat) {
        final var sql = "delete from users_chats where user_id = ? and chat_id = ?";
        jdbcTemplate.update(sql, user.getId(), chat.getId());
    }

    @Override
    public boolean isUserExcludedInChat(final User user, final Chat chat) {
        final var sql = "select is_excluded from users_chats where user_id = ? and chat_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, user.getId(), chat.getId()));
    }

    @Override
    public void toggleIsExcluded(final User user, final Chat chat) {
        final var sql = "update users_chats set is_excluded = not is_excluded where user_id = ? and chat_id = ?";
        jdbcTemplate.update(sql, user.getId(), chat.getId());
    }

    @Override
    public boolean shouldDeleteMessageFromUserInChat(final Long userId, final Long chatId) {
        final var sql =
                "select not is_active and not is_excluded and is_admin from users_chats join users using (user_id) join chats using (chat_id) where user_id = ? and chat_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, chatId));
    }
}
