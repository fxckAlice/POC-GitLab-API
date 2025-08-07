package org.poc.telegrambot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.OffsetDateTime;

@Service
public class GitLabApiClient {
    private final RestTemplate restTemplate;
    @Value("${gitlab.api.token}")
    private String token;
    @Value("${gitlab.api.gateway.protocol}")
    private String protocol;
    @Value("${gitlab.api.gateway.host}")
    private String host;

    public GitLabApiClient(RestTemplate restTemplate) {
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
    public String getMembersAll(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(protocol + host + "/gateway/members/all", headers);
    }
    public String getMemberByEmail(String email){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/members/email")
                .queryParam("email", email)
                .toUriString(), headers
        );
    }
    public String getMRsAll(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(protocol + host + "/gateway/merge-requests/all", headers);
    }
    public String getMRsByFilter(String email, OffsetDateTime since, OffsetDateTime until){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/merge-requests")
                .queryParam("author_email", email != null ? email : "")
                .queryParam("since", since != null ? since.toString() : "")
                .queryParam("until", until != null ? until.toString() : "")
                .toUriString(), headers
        );
    }
    public String getMRByIid(int iid){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/merge-requests/iid")
                .queryParam("iid", iid)
                .toUriString(), headers
        );
    }
    public String getBranchesAll(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(protocol + host + "/gateway/branches/all", headers);
    }
    public String getBranchByName(String name){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/branches/name")
                .queryParam("name", name)
                .toUriString(), headers
        );
    }
    public String getCommitsByBranchName(String branchName){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/branch/all")
                .queryParam("branch_name", branchName)
                .toUriString(), headers
        );
    }
    public String getCommitsByBranchNameAndFilter(String branchName, String authorEmail){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/branch")
                .queryParam("branch_name", branchName)
                .queryParam("author_email", authorEmail)
                .toUriString(), headers
        );
    }
    public String getCommitsByMRIid(long iid){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/mr/all")
                .queryParam("iid", iid)
                .toUriString(), headers
        );
    }
    public String getCommitsByMRIidAndFilter(long iid, String authorEmail){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/mr")
                .queryParam("iid", iid)
                .queryParam("author_email", authorEmail)
                .toUriString(), headers
        );
    }

}
