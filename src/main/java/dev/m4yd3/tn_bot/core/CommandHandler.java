package dev.m4yd3.tn_bot.core;

import dev.m4yd3.tn_bot.db.repository.ChatRepository;
import dev.m4yd3.tn_bot.db.repository.UserRepository;
import dev.m4yd3.tn_bot.service.ChatService;
import dev.m4yd3.tn_bot.service.UserService;
import dev.m4yd3.tn_bot.util.Command;
import dev.m4yd3.tn_bot.util.Responses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
class CommandHandler {
    private static final int MAX_CHATS_PAGE_SIZE = 97; // MUST BE 1 >= x <= 97

    final TelegramClientWrapper client;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final UserService userService;

    void processCommand(final Update update) {
        final var command = parseCommand(update.getMessage().getText());

        log.info("Command: {}", command);

        switch (command.type()) {
            case START, UNKNOWN -> startCommand(update);
            case CHATS -> chatsCommand(update, command);
            case EXCLUDE -> excludeCommand(update, command);
        }
    }

    private void startCommand(final Update update) {
        if (!update.getMessage().getChat().isUserChat()) return;

        final List<KeyboardRow> rows = List.of(new KeyboardRow(new KeyboardButton(Command.Type.CHATS.label)));
        final var keyboard = new ReplyKeyboardMarkup(rows);
        keyboard.setIsPersistent(true);

        final var response = new SendMessage(String.valueOf(update.getMessage().getChatId()), Responses.COMMANDS_LIST);
        response.setReplyMarkup(keyboard);

        client.sendMessage(response);
    }

    private void chatsCommand(final Update update, final Command command) {
        if (!update.getMessage().getChat().isUserChat()) return;

        final var keyboard = getChatsKeyboard(command);

        if (keyboard == null) {
            final var response =
                    new SendMessage(String.valueOf(update.getMessage().getChatId()), Responses.NO_CHATS_FOUND);
            client.sendMessage(response);
            return;
        }

        final var response = new SendMessage(String.valueOf(update.getMessage().getChatId()), Responses.CHATS_LIST);
        response.setReplyMarkup(keyboard);
        client.sendMessage(response);
    }

    private void excludeCommand(final Update update, final Command command) {
        if (update.getMessage().getChat().isUserChat()) return;

        final var chat = chatService.getOrCreateChatFromTelegram(update.getMessage().getChat());

        final var isAdmin = client.getChatAdministrators(update.getMessage().getChatId())
                .stream()
                .anyMatch((member) -> member.getUser().getId().equals(update.getMessage().getFrom().getId()));
        if (!isAdmin) {
            if (chat.getIsAdmin()) {
                client.deleteMessage(update.getMessage());
            }
            return;
        }

        final var users = userRepository.findAllByUserNameIn(command.args().stream().map(Object::toString).toList());
        var excluded = false;
        for (final var user : users) {
            if (user.getUserName().equals(client.getConfig().getName())) continue;

            userService.toggleIsExcluded(user, chat);
            excluded = true;
        }

        if (!excluded) {
            final var response =
                    new SendMessage(String.valueOf(update.getMessage().getChatId()), Responses.NO_USERS_EXCLUDED);
            client.sendMessage(response);
            return;
        }

        final var response = new SendMessage(String.valueOf(update.getMessage().getChatId()), Responses.USERS_EXCLUDED);
        client.sendMessage(response);
    }

    public void processCallback(final CallbackQuery query) {
        final var command = parseCommand(query.getData());

        log.info("Callback: {}", command);

        switch (command.type()) {
            case CHATS -> chatsCallback(query, command);
            case DELETE -> deleteCallback(query);
        }
    }

    private void deleteCallback(final CallbackQuery query) {
        client.deleteMessage(query.getMessage());
    }

    private void chatsCallback(final CallbackQuery query, final Command command) {
        final var keyboard = getChatsKeyboard(command);

        if (keyboard == null) {
            final var response = new EditMessageText("No chats found.");
            response.setChatId(query.getMessage().getChat().getId());
            response.setMessageId(query.getMessage().getMessageId());
            client.editMessage(response);
            return;
        }

        final var response = new EditMessageReplyMarkup();
        response.setChatId(query.getMessage().getChat().getId());
        response.setMessageId(query.getMessage().getMessageId());
        response.setReplyMarkup(keyboard);
        client.editMessage(response);
    }

    private InlineKeyboardMarkup getChatsKeyboard(final Command command) {
        final var page = Math.max(1, (int) command.args().get(0));

        final var chats = chatRepository.findAllByInviteLinkIsNotNullAndIsAdminTrueOrderByTitle(Pageable.ofSize(
                MAX_CHATS_PAGE_SIZE).withPage(page - 1));

        if (!chats.hasContent()) return null;

        final List<InlineKeyboardRow> rows = new ArrayList<>();
        final var buttonsRow = new InlineKeyboardRow();

        if (page > 1) {
            final var backward = new InlineKeyboardButton("◀️");
            backward.setCallbackData("/chats " + (page - 1));
            buttonsRow.add(backward);
        }

        final var close = new InlineKeyboardButton("❌");
        close.setCallbackData("/delete");
        buttonsRow.add(close);

        if (chats.hasNext()) {
            final var forward = new InlineKeyboardButton("▶️");
            forward.setCallbackData("/chats " + (page + 1));
            buttonsRow.add(forward);
        }

        for (final var chat : chats.getContent()) {
            final var button = new InlineKeyboardButton(chat.getTitle());
            button.setUrl(chat.getInviteLink());
            rows.add(new InlineKeyboardRow(button));
        }

        rows.add(buttonsRow);

        return new InlineKeyboardMarkup(rows);
    }

    Command parseCommand(final String command) {
        final var parts = command.split(" ");
        final var type = Command.Type.fromString(parts[0], client.getConfig().getName());

        final var args = List.of(Arrays.copyOfRange(parts, 1, parts.length));
        List<Object> parsedArgs = new ArrayList<>();

        if (type.equals(Command.Type.CHATS)) {
            try {
                parsedArgs.add(Integer.parseUnsignedInt(args.isEmpty() ? "1" : args.get(0)));
            } catch (final NumberFormatException e) {
                parsedArgs.add(1);
            }
        } else if (type.equals(Command.Type.EXCLUDE)) {
            parsedArgs.addAll(args.stream().map((String mention) -> mention.replace("@", "")).distinct().toList());
        } else {
            parsedArgs = Arrays.asList(args.toArray());
        }

        return new Command(type, parsedArgs);
    }
}
