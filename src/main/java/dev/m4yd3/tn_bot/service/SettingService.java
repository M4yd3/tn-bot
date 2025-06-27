package dev.m4yd3.tn_bot.service;

import dev.m4yd3.tn_bot.db.entity.Setting;

import java.time.Duration;
import java.util.regex.Pattern;

public interface SettingService {
    Duration getRegistrationLimitsResetDuration();

    Duration getRegistrationBlockDuration();

    Duration getUserRegistrationTimeoutDuration();

    Duration getChatRegistrationTimeoutDuration();

    Pattern getRegistrationEmailPattern();

    Integer getRegistrationEmailAttemptsLimit();

    Integer getRegistrationCodeAttemptsLimit();

    Setting getSettingByName(String name);

    void saveSetting(Setting setting);
}
