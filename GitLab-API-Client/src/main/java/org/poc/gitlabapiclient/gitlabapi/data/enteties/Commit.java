package org.poc.gitlabapiclient.gitlabapi.data.enteties;

import java.time.OffsetDateTime;

public record Commit(
        String shortId,
        String author,
        String message,
        OffsetDateTime createdAt,
        String webUrl
) {
}
