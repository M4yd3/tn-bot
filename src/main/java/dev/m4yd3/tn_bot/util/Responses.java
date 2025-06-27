package dev.m4yd3.tn_bot.util;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Responses {
    public static final String REGISTERED = "Вы зарегистрированы в системе.";
    public static final String ENTER_EMAIL =
            "Здравствуйте, это корпоративный бот! Пожалуйста, введите вашу корпоративную почту.";
    public static final String INVALID_EMAIL_TRY_AGAIN = "Почта неверна. Попробуйте снова.";
    public static final String ENTER_CODE = "На вашу почту был отправлен код подтверждения. Пожалуйста, введите его.";
    public static final String INVALID_CODE_TRY_AGAIN = "Код неверен. Попробуйте снова.";
    public static final String USER_EXISTS =
            "Пользователь с такой почтой уже существует. Попробуйте другую почту или открепите старый телеграм аккаунт.";
    public static final String SUCCESS = "Вы успешно зарегистрировались.";
    public static final String ERROR = "Произошла непредвиденная ошибка.";
    public static final String GIVE_ADMIN =
            "Пожалуйста, дайте мне права администратора, чтобы я мог правильно функционировать.";
    public static final String UNKNOWN_COMMAND = "Неизвестная команда. Попробуйте снова.";
    public static final String USERS_EXCLUDED = "Пользователи успешно добавлены в исключения.";
    public static final String NO_USERS_EXCLUDED = "0 пользователей были добавлены в исключения.";
    public static final String NO_CHATS_FOUND = "Нет доступных чатов.";
    public static final String CHATS_LIST = "Вот список доступных чатов:";
    public static final String COMMANDS_LIST = "Вот список доступных команд.";

    public static final String MARKDOWN_PARSE_MODE = "Markdown";

    public static String blocked(final Instant instant) {
        return String.format("Вы заблокированы до %s по МСК.", formatTime(instant));
    }

    public static String emailLimitExceeded(final Instant instant) {
        return "Почта неверна. Вы превысили количество попыток. " + blocked(instant);
    }

    public static String codeLimitExceeded(final Instant instant) {
        return "Код неверен. Вы превысили количество попыток. " + blocked(instant);
    }

    public static String chatRegistrationWarning(final Instant instant, final String botLink) {
        return """
               Чат находится под управлением корпоративного бота.
               Всем участникам необходимо зарегистрироваться до %s
               Это можно сделать здесь: %s""".formatted(formatTime(instant), botLink);
    }

    public static String welcomeRegisteredUsers(final ArrayList<String> names) {
        final var mentions = names.stream().map(s -> "@" + s).collect(Collectors.joining(" "));
        return "Добро пожаловать в чат, %s!".formatted(mentions);
    }

    public static String welcomeUnregisteredUsers(final String botLink, final ArrayList<String> names) {
        final var mentions = names.stream().map(s -> "@" + s).collect(Collectors.joining(" "));
        return """
               Добро пожаловать в чат, %s!
               Вам необходимо зарегистрироваться в боте.
               Это можно сделать здесь: %s
               """.formatted(mentions, botLink);
    }

    private static LocalDateTime formatTime(final Instant instant) {
        return LocalDateTime.ofInstant(instant.truncatedTo(ChronoUnit.SECONDS), ZoneId.of("Europe/Moscow"));
    }
}
