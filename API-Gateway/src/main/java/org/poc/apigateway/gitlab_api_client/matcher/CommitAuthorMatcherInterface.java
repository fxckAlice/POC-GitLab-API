package org.poc.apigateway.gitlab_api_client.matcher;

public interface CommitAuthorMatcherInterface {
    long matchEmail(String authorEmail);
    String matchId(long authorId);
}
