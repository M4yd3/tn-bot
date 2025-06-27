package dev.m4yd3.tn_bot.util;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;
import java.util.stream.Stream;

public record Command(Command.Type type, List<Object> args) {
    public enum Type {
        START("/start", "Показать стартовое меню."),
        CHATS("/chats", "Показать управляемые чаты."),
        LEAVE("/leave", "Покинуть все чаты."),
        UNLINK("/unlink", "Отвязать аккаунт."),
        EXCLUDE("/exclude", "Добавить пользователя в исключения.", true),
        DELETE("/delete", "Delete bot message."),
        UNKNOWN("unknown", "Unknown command");

        public final String label;
        public final String description;
        public final boolean isGroup;

        Type(final String label, final String description) {
            this.label = label;
            this.description = description;
            this.isGroup = false;
        }

        Type(final String label, final String description, final boolean isGroup) {
            this.label = label;
            this.description = description;
            this.isGroup = isGroup;
        }

        public static Type fromString(final String label, final String botName) {
            for (final var command : Type.values()) {
                if (command.isGroup && label.equals(command.label + '@' + botName)) return command;

                if (command.label.equals(label)) return command;
            }

            return UNKNOWN;
        }

        public static List<BotCommand> getDirectCommands() {
            return Stream.of(START, CHATS, LEAVE, UNLINK)
                    .map(type -> new BotCommand(type.label, type.description))
                    .toList();
        }

        public static List<BotCommand> getAdminCommands() {
            return List.of(new BotCommand(EXCLUDE.label, EXCLUDE.description));
        }
    }
}