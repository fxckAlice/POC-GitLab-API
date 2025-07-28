package org.poc.gitlabapiclient.gitlabapi.data.enteties;

public record Branch(
        String name,
        Commit lastCommit,
        String webUrl
) {
}
