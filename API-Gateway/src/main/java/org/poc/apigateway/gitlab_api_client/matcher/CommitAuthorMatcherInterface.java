package org.poc.apigateway.gitlab_api_client.matcher;

public interface CommitAuthorMatcherInterface {
    long match(String authorEmail);
}
