package org.poc.apigateway.gitlab_api_client.matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FileCommitAuthorMatcher implements CommitAuthorMatcherInterface{
    @Override
    public long matchEmail(String authorEmail) {
        ClassPathResource resource = new ClassPathResource("members.json");
        try {
            JsonNode rootNode = parseJson(new String(resource.getInputStream().readAllBytes()));
            for (JsonNode memberNode : rootNode) {
                if (memberNode.get("email").asText().equals(authorEmail)) {
                    return memberNode.get("id").asLong();
                }
            }
            throw new IllegalArgumentException("Author email not found in members.json " + authorEmail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String matchId(long authorId) {
        ClassPathResource resource = new ClassPathResource("members.json");
        try {
            JsonNode rootNode = parseJson(new String(resource.getInputStream().readAllBytes()));
            for (JsonNode memberNode : rootNode) {
                if (memberNode.get("id").asLong() == authorId) {
                    return memberNode.get("email").asText();
                }
            }
            throw new IllegalArgumentException("Author id not found in members.json " + authorId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode parseJson(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @PostConstruct
    private void matcherInit(){
        ClassPathResource resource = new ClassPathResource("members.json");
        if (!resource.exists()) {
            throw new RuntimeException("File members.json not found");
        }
        else {
            System.out.println("File members.json found!");
        }
    }

}
