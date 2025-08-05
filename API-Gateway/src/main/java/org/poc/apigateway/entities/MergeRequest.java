package org.poc.apigateway.entities;

import java.time.Duration;
import java.time.OffsetDateTime;

public record MergeRequest(
        long id,
        int iid,
        String authorEmail,
        String authorUrl,
        String title,
        String state,
        String targetBranchName,
        String targetBranchUrl,
        OffsetDateTime createdAt,
        OffsetDateTime mergedAt,
        int commitsCount,
        Duration timeTaken,
        String webUrl
) {
}
