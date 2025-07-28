package org.poc.gitlabapiclient.gitlabapi.data.enteties;

public record Member(
        int id,
        String username,
        String publicEmail,
        int accessLevel,
        String webUrl
) {
}
