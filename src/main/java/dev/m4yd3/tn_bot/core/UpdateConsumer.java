package dev.m4yd3.tn_bot.core;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.db.repository.ChatRepository;
import dev.m4yd3.tn_bot.service.*;
import dev.m4yd3.tn_bot.util.Responses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final RegistrationService registrationService;
    private final TelegramClientWrapper client;
    private final SettingService settingService;
    private final TaskService taskService;
    private final UserService userService;
    private final ChatService chatService;
    private final CommandHandler commandHandler;
    private final ChatRepository chatRepository;

    @Override
    public void consume(final Update update) {
        final var event = parseUpdateType(update);

        log.info("Event: {}", event);

        switch (event) {
            case UNKNOWN -> handleUnknown(update);
            case DIRECT_MESSAGE -> handleDirectMessage(update);
            case GROUP_MESSAGE -> handleGroupMessage(update);
            case BOT_STATUS_CHANGE -> handleBotStatusChange(update);
            case USER_JOINED -> handleUserJoin(update);
            case USER_LEFT -> handleUserLeft(update);
            case COMMAND_CALLBACK -> commandHandler.processCallback(update.getCallbackQuery());
        }
    }

    private Event parseUpdateType(final Update update) {
        if (update.hasMessage()) {
            final Message message = update.getMessage();

            if (message.hasText()) {
                if (message.isUserMessage()) return Event.DIRECT_MESSAGE;

                return Event.GROUP_MESSAGE;
            }

            final boolean hasNewMembers = message.getNewChatMembers() != null && !message.getNewChatMembers().isEmpty();

            if (hasNewMembers) {
                final boolean isNotBot =
                        !message.getNewChatMembers().get(0).getUserName().equals(client.getConfig().getName());
                if (isNotBot) return Event.USER_JOINED;
            }

            if (message.getLeftChatMember() != null && !message.getLeftChatMember().getIsBot()) return Event.USER_LEFT;
        }

        if (update.hasMyChatMember()) {
            final var botMember = update.getMyChatMember();

            final var oldStatus = botMember.getOldChatMember().getStatus();
            final var newStatus = botMember.getNewChatMember().getStatus();

            if (!oldStatus.equals(newStatus)) return Event.BOT_STATUS_CHANGE;
        }

        if (update.hasCallbackQuery()) return Event.COMMAND_CALLBACK;

        return Event.UNKNOWN;
    }

    private void handleUnknown(final Update update) {
        log.info("Unknown event: {}", update);
    }

    private void handleDirectMessage(final Update update) {
        final var message = update.getMessage();

        if (message.getText().startsWith("/start ")) {
            final var decoded = Base64.getDecoder().decode(message.getText().substring(7));
            final var telegramChatId = Long.valueOf(new String(decoded, StandardCharsets.UTF_8));

            final var user = userService.getOrCreateUserFromTelegram(message.getFrom());
            final var chat = chatService.getOrCreateChatFromTelegram(telegramChatId);
            userService.saveUserToChat(user, chat);
        }

        if (!registrationService.isUserRegistered(message.getFrom().getId())) {
            registrationService.handleRegistrationFlow(message);
            return;
        }

        commandHandler.processCommand(update);
    }

    private void handleGroupMessage(final Update update) {
        final var message = update.getMessage();
        final var user = userService.getOrCreateUserFromTelegram(message.getFrom());
        final var chat = chatService.getOrCreateChatFromTelegram(message.getChat());

        userService.saveUserToChat(user, chat);

        final var shouldDelete = userService.shouldDeleteMessageFromUserInChat(user.getId(), chat.getId());

        if (shouldDelete) {
            client.deleteMessage(message);
            return;
        }

        commandHandler.processCommand(update);
    }

    private void handleBotStatusChange(final Update update) {
        final var botMember = update.getMyChatMember();
        final var telegramChat = botMember.getChat();
        final var oldMember = botMember.getOldChatMember();
        final var newMember = botMember.getNewChatMember();

        final var chat = chatService.getOrCreateChatFromTelegramWithAdmin(
                telegramChat,
                newMember.getStatus().equals(Status.ADMIN)
        );

        final boolean botJoinedChat =
                Status.isNotInChat(oldMember.getStatus()) && Status.isInChat(newMember.getStatus());
        if (botJoinedChat && !chat.getIsAdmin()) {
            final var message = new SendMessage(String.valueOf(telegramChat.getId()), Responses.GIVE_ADMIN);
            client.sendMessage(message);
            return;
        }

        if (!chat.getIsAdmin()) return;

        final var chatWithInviteLink = client.getChat(chat.getTelegramId());
        if (chatWithInviteLink != null) {
            chat.setInviteLink(chatWithInviteLink.getInviteLink());
            chatRepository.save(chat);
        }

        startSweep(chat);
    }

    private void startSweep(final Chat chat) {
        final var task = taskService.createTask(chat);

        if (task.isEmpty()) return;

        final Instant sweepFinishTime =
                task.get().getStartedAt().plus(settingService.getChatRegistrationTimeoutDuration());

        final var botLink = client.getConfig().getBotLinkForChat(chat.getTelegramId());
        final var text = Responses.chatRegistrationWarning(sweepFinishTime, botLink);
        final var message = new SendMessage(String.valueOf(chat.getTelegramId()), text);
        message.setParseMode(Responses.MARKDOWN_PARSE_MODE);
        client.sendMessage(message);
    }

    private void handleUserJoin(final Update update) {
        final ArrayList<String> registeredNames = new ArrayList<>();
        final ArrayList<String> unregisteredNames = new ArrayList<>();

        final var chat = chatService.getOrCreateChatFromTelegram(update.getMessage().getChat());
        final var users = userService.getOrCreateUsersFromTelegram(update.getMessage().getNewChatMembers());

        userService.saveUsersToChat(users, chat);

        for (final var user : users) {
            if (user.getIsActive() || userService.isUserExcludedInChat(user, chat)) {
                registeredNames.add(user.getUserName());
                continue;
            }

            unregisteredNames.add(user.getUserName());

            taskService.createTask(user);
        }

        if (!registeredNames.isEmpty()) {
            final String text = Responses.welcomeRegisteredUsers(registeredNames);
            final var message = new SendMessage(String.valueOf(chat.getTelegramId()), text);
            client.sendMessage(message);
        }

        if (!unregisteredNames.isEmpty()) {
            final var botLink = client.getConfig().getBotLinkForChat(chat.getTelegramId());
            final String text = Responses.welcomeUnregisteredUsers(botLink, unregisteredNames);
            final var message = new SendMessage(String.valueOf(chat.getTelegramId()), text);
            message.setParseMode(Responses.MARKDOWN_PARSE_MODE);
            client.sendMessage(message);
        }
    }

    private void handleUserLeft(final Update update) {
        final var user = userService.getOrCreateUserFromTelegram(update.getMessage().getLeftChatMember());
        final var chat = chatService.getOrCreateChatFromTelegram(update.getMessage().getChat());

        userService.deleteUserFromChat(user, chat);
    }

    private enum Event {
        UNKNOWN, DIRECT_MESSAGE, GROUP_MESSAGE, BOT_STATUS_CHANGE, USER_JOINED, USER_LEFT, COMMAND_CALLBACK
    }

    @SuppressWarnings("unused")
    private static class Status {
        public static final String OWNER = "creator";
        public static final String ADMIN = "administrator";
        public static final String MEMBER = "member";
        public static final String RESTRICTED = "restricted";
        public static final String LEFT = "left";
        public static final String BANNED = "kicked";

        public static boolean isInChat(final String status) {
            return !status.equals(LEFT) && !status.equals(BANNED);
        }

        public static boolean isNotInChat(final String status) {
            return status.equals(LEFT) || status.equals(BANNED);
        }
    }
}
