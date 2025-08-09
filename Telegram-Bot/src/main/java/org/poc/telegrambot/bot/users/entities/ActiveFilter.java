package org.poc.telegrambot.bot.users.entities;

import java.time.OffsetDateTime;

public record ActiveFilter(
        String email,
        String branchName,
        int mrsIid,
        OffsetDateTime since,
        OffsetDateTime until
) {
}
