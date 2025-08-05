package org.poc.apigateway.entities;

import java.time.Duration;
import java.time.OffsetDateTime;

public record Commit(
        String shortId,
        String authorEmail,
        String authorUrl,
        String commitMessage,
        Duration timeTaken,
        OffsetDateTime createdAt,
        String webUrl
) {
}
