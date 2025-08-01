package org.poc.apigateway.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poc.apigateway.entities.Member;
import org.poc.apigateway.gitlab_api_client.GitLabApiClientService;
import org.poc.apigateway.gitlab_api_client.matcher.CommitAuthorMatcher;
import org.poc.apigateway.gitlab_api_client.matcher.FileCommitAuthorMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ResponseConstructingService {
    private final GitLabApiClientService gitLabApiClientService;
    private final CommitAuthorMatcher fileCommitAuthorMatcher;

    public ResponseConstructingService(GitLabApiClientService gitLabApiClientService, @Autowired FileCommitAuthorMatcher fileCommitAuthorMatcher) {
        this.gitLabApiClientService = gitLabApiClientService;
        this.fileCommitAuthorMatcher = fileCommitAuthorMatcher;
    }

    private JsonNode parseJson(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public List<Member> getMembers() {
        JsonNode members = parseJson(gitLabApiClientService.getMembers()),
                mrAll = parseJson(gitLabApiClientService.getMRsAll());

        List<Member> result = new ArrayList<>();

        members.forEach(memberNode -> {
            long id = memberNode.get("id").asLong();

            AtomicInteger mrsByAuthorId = new AtomicInteger();
            mrAll.forEach(mrNode -> {
                if (mrNode.get("authorId").asLong() == id) {
                    mrsByAuthorId.getAndIncrement();
                }
            });

            result.add(new Member(
                    id,
                    memberNode.get("username").asText(),
                    fileCommitAuthorMatcher.matchId(id),
                    memberNode.get("accessLevel").asInt(),
                    memberNode.get("webUrl").asText(),
                    mrsByAuthorId.get()
            ));
        });

        return result;
    }
}
