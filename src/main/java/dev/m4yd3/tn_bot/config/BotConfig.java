package dev.m4yd3.tn_bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Base64;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "bot")
@PropertySource("classpath:application.yaml")
@Data
public class BotConfig {
    String name;

    String token;

    public String getBotLink() {
        return "@" + name;
    }

    public String getBotLink(Map<String, String> params) {
        final var url = new StringBuilder("https://t.me/" + name);

        var isFirst = true;

        for (final var param : params.entrySet()) {
            if (param.getKey() == null || param.getKey().isEmpty() || param.getValue() == null ||
                    param.getValue().isEmpty()) {
                continue;
            }

            final var value = Base64.getEncoder().encodeToString(param.getValue().getBytes());
            url.append(isFirst ? "?" : "&").append(param.getKey()).append("=").append(value);

            isFirst = false;
        }

        return "[@%s](%s)".formatted(name, url.toString());
    }

    public String getBotLink(String param, String value) {
        return getBotLink(Map.of(param, value));
    }

    public String getBotLinkForChat(Long chatId) {
        return getBotLink("start", String.valueOf(chatId));
    }
}
