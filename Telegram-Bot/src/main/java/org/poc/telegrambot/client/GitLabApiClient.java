package org.poc.telegrambot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    public String getMembersAll() throws HttpClientErrorException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(protocol + host + "/gateway/members/all", headers);
    }
    public String getMemberByEmail(String email) throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/members/email")
                .queryParam("email", email)
                .toUriString(), headers
        );
    }
    public String getMRsAll() throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(protocol + host + "/gateway/merge-requests/all", headers);
    }
    public String getMRsByFilter(String email, OffsetDateTime since, OffsetDateTime until) throws HttpClientErrorException{
        if ((email == null || email.isEmpty()) && since == null && until == null) {
            throw new IllegalArgumentException("At least one filter must be specified.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/merge-requests")
                .queryParam("author_email", email != null ? email : "")
                .queryParam("since", since != null ? since : "")
                .queryParam("until", until != null ? until : "")
                .toUriString(), headers
        );
    }
    public String getMRByIid(int iid) throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/merge-requests/iid")
                .queryParam("iid", iid)
                .toUriString(), headers
        );
    }
    public String getBranchesAll() throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(protocol + host + "/gateway/branches/all", headers);
    }
    public String getBranchByName(String name) throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/branches/name")
                .queryParam("name", name)
                .toUriString(), headers
        );
    }
    public String getCommitsByBranchName(String branchName) throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/branch/all")
                .queryParam("branch_name", branchName)
                .toUriString(), headers
        );
    }
    public String getCommitsByBranchNameAndFilter(String branchName, String authorEmail) throws HttpClientErrorException{
        if ((authorEmail == null || authorEmail.isEmpty()) && (branchName == null || branchName.isEmpty())) {
            throw new IllegalArgumentException("At least one filter must be specified.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/branch")
                .queryParam("branch_name", branchName)
                .queryParam("author_email", authorEmail)
                .toUriString(), headers
        );
    }
    public String getCommitsByMRIid(long iid) throws HttpClientErrorException{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/mr/all")
                .queryParam("mr_iid", iid)
                .toUriString(), headers
        );
    }
    public String getCommitsByMRIidAndFilter(long iid, String authorEmail) throws HttpClientErrorException{
        if ((authorEmail == null || authorEmail.isEmpty()) && (iid == 0)) {
            throw new IllegalArgumentException("At least one filter must be specified.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return fetch(UriComponentsBuilder.fromUriString(protocol + host + "/gateway/commits/mr")
                .queryParam("iid", iid)
                .queryParam("author_email", authorEmail)
                .toUriString(), headers
        );
    }

}
