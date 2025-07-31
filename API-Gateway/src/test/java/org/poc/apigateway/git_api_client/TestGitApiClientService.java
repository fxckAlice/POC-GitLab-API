package org.poc.apigateway.git_api_client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.poc.apigateway.gitlab_api_client.GitLabApiClientService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TestGitApiClientService {
    @Mock
    private RestTemplate restTemplate;

    private GitLabApiClientService gitLabApiClientService;
    @SuppressWarnings("all")
    private final String protocol = "http://";
    private final String host = "test-host";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitLabApiClientService = new GitLabApiClientService(restTemplate);

        setField(gitLabApiClientService, "protocol", protocol);
        setField(gitLabApiClientService, "host", host);
        String token = "test-token";
        setField(gitLabApiClientService, "token", token);
    }
    @Test
    void testGetMembers() {
        String expectedResponse = "[{id: 1, name: \"<NAME>\"}, {id: 2, name: \"<NAME>\"}]";
        String url = protocol + host + "/gateway/members/all";
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiClientService.getMembers();

        assertEquals(expectedResponse, actualResponse);
        System.out.println(actualResponse);
    }
    @Test
    void testGetMRsAll() {
        String expectedResponse = "[{id: 1, title: \"Test MR 1\"}, {id: 2, title: \"Test MR 2\"}]";
        String url = protocol + host + "/gateway/merge-requests/all";
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiClientService.getMRsAll();

        assertEquals(expectedResponse, actualResponse);
        System.out.println(actualResponse);
    }
    @Test
    void testGetMRsByAuthorId() {
        String expectedResponse = "[{id: 1, title: \"Test MR 1\"}, {id: 2, title: \"Test MR 2\"}]";
        long authorId = 1;
        String url = protocol + host + "/gateway/merge-requests?author_id=" + authorId;
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiClientService.getMRsByAuthorId(authorId);

        assertEquals(expectedResponse, actualResponse);
        System.out.println(actualResponse);
    }
    @Test
    void testGetBranchesAll() {
        String expectedResponse = "[{id: 1, name: \"test-branch\"}, {id: 2, name: \"main\"}]";
        String url = protocol + host + "/gateway/branches/all";
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiClientService.getBranchesAll();

        assertEquals(expectedResponse, actualResponse);
        System.out.println(actualResponse);
    }
    @Test
    void testGetCommitsByMergeRequestIid() {
        String expectedResponse = "[{id: 1, message: \"Test commit 1\"}, {id: 2, message: \"Test commit 2\"}]";
        long mergeRequestId = 1;
        String url = protocol + host + "/gateway/commits/mr?mr_iid=" + mergeRequestId;
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiClientService.getCommitsByMergeRequestIid(mergeRequestId);

        assertEquals(expectedResponse, actualResponse);
        System.out.println(actualResponse);
    }
    @Test
    void testGetCommitsByBranchName() {
        String expectedResponse = "[{id: 1, message: \"Test commit 1\"}, {id: 2, message: \"Test commit 2\"}]";
        String branchName = "test-branch";
        String url = protocol + host + "/gateway/commits/branch?branch_name=" + branchName;
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiClientService.getCommitsByBranchName(branchName);

        assertEquals(expectedResponse, actualResponse);
        System.out.println(actualResponse);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
