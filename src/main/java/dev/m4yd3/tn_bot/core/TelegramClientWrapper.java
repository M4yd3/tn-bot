package dev.m4yd3.tn_bot.core;

import dev.m4yd3.tn_bot.config.BotConfig;
import dev.m4yd3.tn_bot.util.Command;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.chat.ChatFullInfo;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Getter
@Slf4j
@Component
public class TelegramClientWrapper extends OkHttpTelegramClient implements TelegramClient {
    private final BotConfig config;

    public TelegramClientWrapper(final BotConfig config) {
        super(config.getToken());
        this.config = config;
    }

    public void sendMessage(final SendMessage message) {
        try {
            execute(message);
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteMessage(final Message message) {
        try {
            final var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());
            execute(deleteMessage);
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteMessage(final MaybeInaccessibleMessage message) {
        try {
            final var deleteMessage = new DeleteMessage(String.valueOf(message.getChatId()), message.getMessageId());
            execute(deleteMessage);
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public boolean banUserFromChat(final Long userTelegramId, final Long chatTelegramId) {
        try {
            final var banMessage = new BanChatMember(String.valueOf(chatTelegramId), userTelegramId);

            return execute(banMessage);
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void setBotCommands() {
        try {
            execute(new DeleteMyCommands(new BotCommandScopeDefault(), null));
            execute(new SetMyCommands(Command.Type.getDirectCommands(), new BotCommandScopeAllPrivateChats(), null));
            execute(new SetMyCommands(
                    Command.Type.getAdminCommands(),
                    new BotCommandScopeAllChatAdministrators(),
                    null
            ));
        } catch (final TelegramApiException e) {
            log.error("Unable to set bot commands.", e);
        }
    }

    public ChatFullInfo getChat(final Long chatTelegramId) {
        try {
            return execute(new GetChat(String.valueOf(chatTelegramId)));
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<ChatMember> getChatAdministrators(final Long chatTelegramId) {
        try {
            return execute(new GetChatAdministrators(String.valueOf(chatTelegramId)));
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void editMessage(final EditMessageReplyMarkup message) {
        try {
            execute(message);
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void editMessage(final EditMessageText message) {
        try {
            execute(message);
        } catch (final TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
