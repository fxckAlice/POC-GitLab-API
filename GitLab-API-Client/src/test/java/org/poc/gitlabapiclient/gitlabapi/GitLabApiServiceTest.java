package org.poc.gitlabapiclient.gitlabapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitLabApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GitLabApiService gitLabApiService;

    private final String baseUrl = "https://gitlab.com/api/v4/projects/";
    private final String projectId = "123456";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitLabApiService = new GitLabApiService(restTemplate);

        setField(gitLabApiService, "baseUrl", baseUrl);
        String token = "test-token";
        setField(gitLabApiService, "token", token);
        setField(gitLabApiService, "projectId", projectId);
    }

    @Test
    void testGetMembers() {
        String expectedResponse = "[{\"id\":1,\"name\":\"John\"}]";
        String url = baseUrl + projectId + "/members/all";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiService.getMembers();

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testGetMergeRequestsAll() {
        String expectedResponse = "[{\"id\":1,\"iid\":1,\"author\":{\"id\":1,\"username\":\"John\"},\"title\":\"Test MR\",\"state\":\"opened\",\"target_branch\":\"master\",\"created_at\":\"2021-08-17T10:00:00+03:00\",\"web_url\":\"https://gitlab.com/test/test-project/-/merge_requests/1\"}]";
        String url = baseUrl + projectId + "/merge_requests";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiService.getAllMergeRequests();

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }
    @Test
    void testGetMergeRequestsByAuthorId() {
        String expectedResponse = "[{\"id\":1,\"iid\":1,\"author\":{\"id\":1,\"username\":\"John\"},\"title\":\"Test MR\",\"state\":\"opened\",\"target_branch\":\"master\",\"created_at\":\"2021-08-17T10:00:00+03:00\",\"web_url\":\"https://gitlab.com/test/test-project/-/merge_requests/1\"}]";
        String url = baseUrl + projectId + "/merge_requests?author_id=1";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiService.getMergeRequestsByAuthorId(1);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testGetBranches() {
        String expectedResponse = "[{\"name\":\"master\",\"commit\":{\"short_id\":\"f8fd8d9\",\"author_name\":\"John\",\"message\":\"Test commit\",\"created_at\":\"2021-08-17T10:00:00+03:00\"},\"web_url\":\"https://gitlab.com/test/test-project/-/commit/1234567890\"}]";
        String url = baseUrl + projectId + "/repository/branches";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiService.getBranches();

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testGetCommitsByMergeRequestIid() {
        String expectedResponse = "[{\"short_id\":\"f7f7d8d\",\"author_name\":\"John\",\"message\":\"Test commit\",\"created_at\":\"2021-08-17T10:00:00+03:00\",\"web_url\":\"https://gitlab.com/test/test-project/-/commit/1234567890\"}]";
        String url = baseUrl + projectId + "/merge_requests/1/commits";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiService.getCommitsByMergeRequestIid(1);

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testGetCommitsByBranchName() {
        String expectedResponse = "[{\"short_id\":\"f7f7d8d\",\"author_name\":\"John\",\"message\":\"Test commit\",\"created_at\":\"2021-08-17T10:00:00+03:00\",\"web_url\":\"https://gitlab.com/test/test-project/-/commit/1234567890\"}]";
        String url = baseUrl + projectId + "/repository/commits?ref_name=master";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = gitLabApiService.getCommitsByBranchName("master");

        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
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
