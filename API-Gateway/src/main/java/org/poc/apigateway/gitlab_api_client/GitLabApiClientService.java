package org.poc.apigateway.gitlab_api_client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GitLabApiClientService {
    @Value("${gitlab.api.client.protocol}")
    private String protocol;
    @Value("${gitlab.api.client.host}")
    private String host;
    @Value("${gitlab.api.token}")
    private String token;
    private final RestTemplate restTemplate;

    public GitLabApiClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private String fetch(String url){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
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
        String url = protocol + host + "/gateway/members/all";
        return fetch(url);
    }
    public String getMRsAll(){
        String url = protocol + host + "/gateway/merge-requests/all";
        return fetch(url);
    }
    public String getMRsByAuthorId(long authorId){
        String url = UriComponentsBuilder.fromUriString(protocol + host + "/gateway/merge-requests")
                .queryParam("author_id", authorId)
                .toUriString();
        return fetch(url);
    }
    public String getBranchesAll(){
        String url = protocol + host + "/gateway/branches/all";
        return fetch(url);
    }
    public String getCommitsByMergeRequestIid(long mergeRequestId){
        String url = UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/mr")
                .queryParam("mr_iid", mergeRequestId)
                .toUriString();
        return fetch(url);
    }
    public String getCommitsByBranchName(String branchName){
        String url = UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/branch")
                .queryParam("branch_name", branchName)
                .toUriString();
        return fetch(url);
    }

}
