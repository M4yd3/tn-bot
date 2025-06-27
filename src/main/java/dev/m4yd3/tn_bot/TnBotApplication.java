package dev.m4yd3.tn_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class TnBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TnBotApplication.class, args);
    }
}
