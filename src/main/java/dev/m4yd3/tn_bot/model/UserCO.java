package dev.m4yd3.tn_bot.model;

import java.sql.Timestamp;

public record UserCO(
        Long id,
        String email,
        Boolean isActive,
        String firstName,
        String lastName,
        String middleName,
        Timestamp registeredAt,
        String userName,
        Boolean isExcluded
) {
}

