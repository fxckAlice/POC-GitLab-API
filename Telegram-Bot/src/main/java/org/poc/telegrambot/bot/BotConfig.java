package org.poc.telegrambot.bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class BotConfig {
    @Setter
    @Getter
    private String token;
    @Setter
    @Getter
    private String username;

}
