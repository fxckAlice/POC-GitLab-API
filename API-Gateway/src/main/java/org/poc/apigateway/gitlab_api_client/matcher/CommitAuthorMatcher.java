package org.poc.apigateway.gitlab_api_client.matcher;

public interface CommitAuthorMatcher {
    long matchEmail(String authorEmail);
    String matchId(long authorId);
}
