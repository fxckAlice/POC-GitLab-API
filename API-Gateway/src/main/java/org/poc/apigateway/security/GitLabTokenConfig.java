package org.poc.apigateway.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class GitLabTokenConfig {

    @Value("${gitlab.api.token}")
    private String rawToken;

    @Getter
    private String hashedToken;

    @PostConstruct
    public void init() {
        this.hashedToken = new BCryptPasswordEncoder().encode(rawToken);
    }

}
