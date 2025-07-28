package org.poc.gitlabapiclient.gitlabapi.data.enteties;

import java.time.OffsetDateTime;

public record MergeRequest(
        int id,
        int iid,
        int authorId,
        String title,
        String state,
        String targetBranch,
        OffsetDateTime createdAt,
        OffsetDateTime mergedAt,
        String webUrl
) {
}
