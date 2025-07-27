package org.poc.gitlabapiclient.gitlabapi.data.enteties;

import java.time.LocalDateTime;

public record Commit(
        String shortId,
        String author,
        String message,
        LocalDateTime createdAt,
        String webUrl
) {
}
