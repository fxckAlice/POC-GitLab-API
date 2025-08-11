package org.poc.telegrambot.bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Setter
    @Getter
    @Value("${telegram.bot.token}")
    private String token;
    @Setter
    @Getter
    @Value("${telegram.bot.username}")
    private String username;

}
