package org.poc.telegrambot.bot.users.entities;

import java.time.OffsetDateTime;

public record ActiveFilter(
        String email,
        String branchName,
        int mrIid,
        OffsetDateTime since,
        OffsetDateTime until
) {
}
