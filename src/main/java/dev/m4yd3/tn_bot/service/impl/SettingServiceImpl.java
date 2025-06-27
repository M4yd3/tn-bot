package dev.m4yd3.tn_bot.service.impl;

import dev.m4yd3.tn_bot.db.entity.Setting;
import dev.m4yd3.tn_bot.db.repository.SettingRepository;
import dev.m4yd3.tn_bot.service.SettingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingServiceImpl implements SettingService {
    final Map<String, Setting> cache = new HashMap<>();
    private final SettingRepository settingRepository;

    @Override
    public Duration getRegistrationLimitsResetDuration() {
        return Duration.parse(getSettingByName("REGISTRATION_LIMITS_RESET_DURATION").getValue());
    }

    @Override
    public Duration getRegistrationBlockDuration() {
        return Duration.parse(getSettingByName("REGISTRATION_BLOCK_DURATION").getValue());
    }

    @Override
    public Duration getUserRegistrationTimeoutDuration() {
        return Duration.parse(getSettingByName("USER_REGISTRATION_TIMEOUT_DURATION").getValue());
    }

    @Override
    public Duration getChatRegistrationTimeoutDuration() {
        return Duration.parse(getSettingByName("CHAT_REGISTRATION_TIMEOUT_DURATION").getValue());
    }

    @Override
    public Pattern getRegistrationEmailPattern() {
        return Pattern.compile(getSettingByName("REGISTRATION_EMAIL_PATTERN").getValue());
    }

    @Override
    public Integer getRegistrationEmailAttemptsLimit() {
        return Integer.valueOf(getSettingByName("REGISTRATION_EMAIL_ATTEMPTS_LIMIT").getValue());
    }

    @Override
    public Integer getRegistrationCodeAttemptsLimit() {
        return Integer.valueOf(getSettingByName("REGISTRATION_CODE_ATTEMPTS_LIMIT").getValue());
    }

    public Setting getSettingByName(String name) {
        if (cache.containsKey(name)) return cache.get(name);

        final var setting = settingRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(String.format("Setting \"%s\" not found", name))
        );

        cache.put(name, setting);

        return setting;
    }

    public void saveSetting(Setting setting) {
        try {
            settingRepository.save(setting);
            cache.put(setting.getName(), setting);
        } catch (Exception e) {
            log.error("Failed to save setting {}", setting, e);
        }
    }
}
