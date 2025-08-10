package org.poc.telegrambot.bot.users.entities;

import java.time.OffsetDateTime;

public record ActiveFilter(
        String email,
        String branchName,
        OffsetDateTime since,
        OffsetDateTime until
) {
}
