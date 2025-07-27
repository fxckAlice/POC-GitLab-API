package org.poc.gitlabapiclient.gitlabapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GitLabApiService {
    private final RestTemplate restTemplate;
    @Value("${gitlab.api.url}")
    private String baseUrl;
    @Value("${gitlab.api.token}")
    private String token;
    @Value("${gitlab.api.project.id}")
    private String projectId;

    public GitLabApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private String fetch(String url, HttpHeaders headers){
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }
    public String getMembers(){
        String url = baseUrl + projectId + "/members/all";
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", token);
        return fetch(url, headers);
    }
    public String getAllMergeRequests() {
        String url = baseUrl + projectId + "/merge_requests";
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", token);
        return fetch(url, headers);
    }
    public String getMergeRequestsByAuthorId(long authorId) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + projectId + "/merge_requests")
                .queryParam("author_id", authorId)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", token);
        return fetch(url, headers);
    }
    public String getBranches(){
        String url = baseUrl + projectId + "/repository/branches";
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", token);
        return fetch(url, headers);
    }
    public String getCommitsByMergeRequestIid(int mergeRequestIid){
        String url = baseUrl + projectId + "/merge_requests/" + mergeRequestIid + "/commits";
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", token);
        return fetch(url, headers);
    }
    public String getCommitsByBranchName(String branchName){
        String url = UriComponentsBuilder.fromUriString(baseUrl + projectId + "/repository/commits")
                .queryParam("ref_name", branchName)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", token);
        return fetch(url, headers);
    }
}
