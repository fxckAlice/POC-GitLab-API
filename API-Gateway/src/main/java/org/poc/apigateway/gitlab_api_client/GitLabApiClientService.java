package org.poc.apigateway.gitlab_api_client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        String url = protocol + host + "/gateway/members/all";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return fetch(url, headers);
    }
}
