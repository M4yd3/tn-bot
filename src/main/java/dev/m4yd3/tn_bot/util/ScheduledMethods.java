package dev.m4yd3.tn_bot.util;

import dev.m4yd3.tn_bot.core.TelegramClientWrapper;
import dev.m4yd3.tn_bot.db.entity.Task;
import dev.m4yd3.tn_bot.db.repository.ChatRepository;
import dev.m4yd3.tn_bot.db.repository.TaskRepository;
import dev.m4yd3.tn_bot.db.repository.UserRepository;
import dev.m4yd3.tn_bot.service.RegistrationService;
import dev.m4yd3.tn_bot.service.SettingService;
import dev.m4yd3.tn_bot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Component
class ScheduledMethods {
    private final TaskRepository taskRepository;
    private final SettingService settingService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final TelegramClientWrapper telegramClientWrapper;
    private final RegistrationService registrationService;
    private final UserService userService;

    @Scheduled(fixedRate = 1000)
    public void runTasks() {
        log.debug("Running tasks...");

        final var now = Instant.now();
        final var tasks = taskRepository.findAll();

        for (final var task : tasks) {
            switch (task.getType()) {
                case USER_REGISTRATION -> processUserRegistrationTask(task, now);
                case CHAT_REGISTRATION -> processChatRegistrationTask(task, now);
                case EMPLOYEE_FIRING -> processFiringTask(task, now);
            }
        }
    }

    private void processUserRegistrationTask(final Task task, final Instant now) {
        final var timeoutDuration = settingService.getUserRegistrationTimeoutDuration();

        if (task.getStartedAt().isAfter(now)) return;

        final var expired =
                Objects.requireNonNullElse(task.getEndsAt(), task.getStartedAt().plus(timeoutDuration)).isAfter(now);
        if (expired) {
            finishUserSweep(Long.valueOf(task.getValue()));
            taskRepository.delete(task);
            return;
        }

        // TODO: check for specific time of day / run a separate task daily?
        final var deleteTask = sweepUser(Long.valueOf(task.getValue()));
        if (deleteTask) taskRepository.delete(task);
    }

    private void processChatRegistrationTask(final Task task, final Instant now) {
        final var timeoutDuration = settingService.getChatRegistrationTimeoutDuration();

        if (task.getStartedAt().isAfter(now)) return;

        final var expired =
                Objects.requireNonNullElse(task.getEndsAt(), task.getStartedAt().plus(timeoutDuration)).isAfter(now);
        if (expired) {
            finishChatSweep(Long.valueOf(task.getValue()));
            taskRepository.delete(task);
            return;
        }

        // TODO: check for specific time of day
        final var deleteTask = sweepChat(Long.valueOf(task.getValue()));
        if (deleteTask) taskRepository.delete(task);
    }

    private void processFiringTask(final Task task, final Instant now) {
        if (task.getStartedAt().isAfter(now)) return;

        if (task.getEndsAt() == null) {
            log.info("Firing task is invalid, endsAt(firing time) is null: {}", task);
            return;
        }

        if (task.getEndsAt().isAfter(now)) return;

        fireUser(Long.valueOf(task.getValue()));
        taskRepository.delete(task);
    }

    private boolean sweepUser(final Long userId) {
        final var userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) return true;

        final var user = userOptional.get();

        if (registrationService.isUserRegistered(user.getTelegramId())) return true;

        // TODO: send a message to the user
        return false;
    }

    private boolean sweepChat(final Long chatId) {
        final var chatOptional = chatRepository.findById(chatId);

        if (chatOptional.isEmpty()) return true;

        final var chat = chatOptional.get();

        final var users = userRepository.getUsersInChat(chat.getId())
                .stream()
                .filter(user -> registrationService.isUserRegistered(user.getTelegramId()) ||
                        userService.isUserExcludedInChat(user, chat))
                .toList();

        if (users.isEmpty()) return true;

        // TODO: send a message to chat
        return false;
    }

    private void finishUserSweep(final Long userId) {
        final var userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) return;

        final var user = userOptional.get();

        if (registrationService.isUserRegistered(user.getTelegramId())) return;

        final var chats = chatRepository.getChatsForUser(user.getId());
        for (final var chat : chats) {
            if (userService.isUserExcludedInChat(user, chat)) continue;

            final var didDelete = telegramClientWrapper.banUserFromChat(user.getTelegramId(), chat.getTelegramId());
            if (didDelete) userService.deleteUserFromChat(user, chat);
        }
    }

    private void finishChatSweep(final Long chatId) {
        final var chatOptional = chatRepository.findById(chatId);

        if (chatOptional.isEmpty()) return;

        final var chat = chatOptional.get();

        final var users = userRepository.getUsersInChat(chat.getId());
        for (final var user : users) {
            final boolean userIsRegistered = registrationService.isUserRegistered(user.getTelegramId());
            final boolean userIsExcludedInChat = userService.isUserExcludedInChat(user, chat);

            if (userIsRegistered || userIsExcludedInChat) continue;

            final var didDelete = telegramClientWrapper.banUserFromChat(user.getTelegramId(), chat.getTelegramId());
            if (didDelete) userService.deleteUserFromChat(user, chat);
        }
    }

    private void fireUser(final Long userId) {
        log.debug("Firing user with id: {}", userId);

        final var userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) return;

        final var user = userOptional.get();

        final var chats = chatRepository.getChatsForUser(user.getId());
        for (final var chat : chats) {
            final var didDelete = telegramClientWrapper.banUserFromChat(user.getTelegramId(), chat.getTelegramId());
            if (didDelete) userService.deleteUserFromChat(user, chat);
        }

        user.setIsActive(false);
        userRepository.save(user);
    }
}
