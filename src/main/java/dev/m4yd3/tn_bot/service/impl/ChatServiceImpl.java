package dev.m4yd3.tn_bot.service.impl;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.db.repository.ChatRepository;
import dev.m4yd3.tn_bot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;

    @Override
    public Chat getOrCreateChatFromTelegram(org.telegram.telegrambots.meta.api.objects.chat.Chat telegramChat) {
        return chatRepository.findByTelegramId(telegramChat.getId())
                .orElseGet(() -> chatRepository.save(new Chat(telegramChat.getId(), telegramChat.getTitle())));
    }

    @Override
    public Chat getOrCreateChatFromTelegram(Long telegramChatId) {
        return chatRepository.findByTelegramId(telegramChatId)
                .orElseGet(() -> chatRepository.save(new Chat(telegramChatId)));
    }

    @Override
    public Chat getOrCreateChatFromTelegramWithAdmin(
            org.telegram.telegrambots.meta.api.objects.chat.Chat telegramChat,
            boolean isAdmin
    ) {
        final var optional = chatRepository.findByTelegramId(telegramChat.getId());

        if (optional.isPresent()) {
            final var chat = optional.get();

            if (chat.getIsAdmin() == isAdmin) return chat;

            chat.setIsAdmin(isAdmin);
            return chatRepository.save(chat);
        }

        return chatRepository.save(new Chat(telegramChat.getId(), telegramChat.getTitle(), isAdmin));
    }


}
