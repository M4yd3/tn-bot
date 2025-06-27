package dev.m4yd3.tn_bot.service;

import dev.m4yd3.tn_bot.db.entity.Chat;
import dev.m4yd3.tn_bot.db.entity.Task;
import dev.m4yd3.tn_bot.db.entity.User;

import java.util.Optional;

public interface TaskService {
    Optional<Task> createTask(String value, Task.Type type);

    Optional<Task> createTask(Chat chat);

    Optional<Task> createTask(User user);
}
