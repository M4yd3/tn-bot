package dev.m4yd3.tn_bot.service.impl;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.db.entity.Task;
import dev.m4yd3.tn_bot.db.entity.User;
import dev.m4yd3.tn_bot.db.repository.TaskRepository;
import dev.m4yd3.tn_bot.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Optional<Task> createTask(String value, Task.Type type) {
        try {
            return taskRepository.findByValueAndType(value, type)
                    .or(() -> Optional.of(taskRepository.save(new Task(value, type))));
        } catch (Exception e) {
            log.error("Error creating task with type {} for value {}", type, value, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Task> createTask(Chat chat) {
        final var value = String.valueOf(chat.getId());

        return createTask(value, Task.Type.CHAT_REGISTRATION);
    }

    @Override
    public Optional<Task> createTask(User user) {
        final var value = String.valueOf(user.getId());

        return createTask(value, Task.Type.USER_REGISTRATION);
    }
}
