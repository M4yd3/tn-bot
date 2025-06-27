package dev.m4yd3.tn_bot.rest;

import dev.m4yd3.tn_bot.core.TelegramClientWrapper;
import dev.m4yd3.tn_bot.db.entity.Setting;
import dev.m4yd3.tn_bot.db.repository.ChatRepository;
import dev.m4yd3.tn_bot.db.repository.SettingRepository;
import dev.m4yd3.tn_bot.db.repository.UserRepository;
import dev.m4yd3.tn_bot.model.ChatCO;
import dev.m4yd3.tn_bot.model.UserCO;
import dev.m4yd3.tn_bot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@org.springframework.web.bind.annotation.RestController
@CrossOrigin({"http://localhost:5173"})
@RequestMapping("/api")
public class RestController {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TelegramClientWrapper client;
    private final SettingRepository settingRepository;

    @GetMapping("/chats")
    public List<ChatCO> getChats() {
        return chatRepository.getChatsForAdmin();
    }

    @GetMapping("/chats/{chatId}")
    public List<UserCO> getChatUsers(@PathVariable final Long chatId) {
        final var chat = chatRepository.findById(chatId);

        if (chat.isEmpty()) return new ArrayList<>();

        return userRepository.findAllInChat(chat.get().getId());
    }

    @DeleteMapping("/chats/{chatId}/{userId}")
    public boolean deleteUserFromChat(@PathVariable final Long chatId, @PathVariable final Long userId) {
        final var user = userRepository.findById(userId);
        final var chat = chatRepository.findById(chatId);

        if (user.isEmpty() || chat.isEmpty() || !chat.get().getIsAdmin()) return false;

        final var didDelete = client.banUserFromChat(user.get().getTelegramId(), chat.get().getTelegramId());
        if (!didDelete) return false;

        userService.deleteUserFromChat(user.get(), chat.get());
        return true;
    }

    @PostMapping("/chats/exclude/{chatId}/{userId}")
    public boolean toggleUserExclusion(@PathVariable final Long chatId, @PathVariable final Long userId) {
        final var user = userRepository.findById(userId);
        final var chat = chatRepository.findById(chatId);

        if (user.isEmpty() || chat.isEmpty()) return false;

        userService.toggleIsExcluded(user.get(), chat.get());
        return true;
    }

    @GetMapping("/settings")
    public List<Setting> getSettings() {
        final List<Setting> settings = new ArrayList<>();
        settingRepository.findAll().forEach(settings::add);

        return settings;
    }

    @PutMapping("/setting")
    public boolean saveSettings(@RequestBody final Setting setting) {
        try {
            settingRepository.save(setting);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }
}