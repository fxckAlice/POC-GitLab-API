package org.poc.apigateway.matcher;

import org.junit.jupiter.api.Test;
import org.poc.apigateway.gitlab_api_client.matcher.FileCommitAuthorMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFileCommitAuthorMatcher {
    @Test
    void testMatch() {
        FileCommitAuthorMatcher matcher = new FileCommitAuthorMatcher();
        long result = matcher.match("g0tmi1k@kali.org");
        assertEquals(2410870, result);
    }
}
