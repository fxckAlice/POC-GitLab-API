package org.poc.gitlabapiclient.gitlabapi.data.enteties;

import java.time.LocalDateTime;

public record MergeRequest(
        int id,
        int iid,
        Member author,
        String title,
        String state,
        String targetBranch,
        LocalDateTime createdAt,
        LocalDateTime mergedAt,
        String webUrl
) {
}
