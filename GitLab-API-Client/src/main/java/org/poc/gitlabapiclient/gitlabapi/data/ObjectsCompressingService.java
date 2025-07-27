package org.poc.gitlabapiclient.gitlabapi.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Branch;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Commit;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Member;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.MergeRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Service
public class ObjectsCompressingService {
    private JsonNode parseJson(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private Member compressMember(JsonNode bigMember) {
        return new Member(
                bigMember.get("id").asInt(),
                bigMember.get("username").asText(),
                bigMember.get("public_email").asText(),
                bigMember.get("access_level").asInt(),
                bigMember.get("web_url").asText()
        );
    }
    private Branch compressBranch(JsonNode bigBranch) {
        return new Branch(
                bigBranch.get("name").asText(),
                compressCommit(bigBranch.get("last_commit")),
                bigBranch.get("web_url").asText()
        );
    }
    private Commit compressCommit(JsonNode bigCommit) {
        return new Commit(
                bigCommit.get("short_id").asText(),
                bigCommit.get("author_name").asText(),
                bigCommit.get("message").asText(),
                LocalDateTime.parse(bigCommit.get("created_at").asText()),
                bigCommit.get("web_url").asText()
        );
    }
    @SuppressWarnings("all")
    private MergeRequest compressMergeRequest(JsonNode bigMergeRequest) {
        String mergedAt = bigMergeRequest.get("merged_at").asText();
        return new MergeRequest(
                bigMergeRequest.get("id").asInt(),
                bigMergeRequest.get("iid").asInt(),
                compressMember(bigMergeRequest.get("author")),
                bigMergeRequest.get("title").asText(),
                bigMergeRequest.get("state").asText(),
                bigMergeRequest.get("target_branch").asText(),
                LocalDateTime.parse(bigMergeRequest.get("created_at").asText()),
                (mergedAt != null || !mergedAt.equals("")) ? LocalDateTime.parse(mergedAt) : null,
                bigMergeRequest.get("web_url").asText()
        );
    }
    public List<Member> compressMembers(String membersString){
        JsonNode members = parseJson(membersString);
        List<Member> result = new ArrayList<>();
        (members).forEach(member -> result.add(compressMember(member)));
        return result;
    }
    public List<MergeRequest> compressMergeRequests(String mergeRequestsString){
        JsonNode mergeRequests = parseJson(mergeRequestsString);
        List<MergeRequest> result = new ArrayList<>();
        mergeRequests.forEach(mergeRequest -> result.add(compressMergeRequest(mergeRequest)));
        return result;
    }
    public List<Branch> compressBranches(String branchesString){
        JsonNode branches = parseJson(branchesString);
        List<Branch> result = new ArrayList<>();
        branches.forEach(branch -> result.add(compressBranch(branch)));
        return result;
    }
    public List<Commit> compressCommits(String commitsString){
        JsonNode commits = parseJson(commitsString);
        List<Commit> result = new ArrayList<>();
        commits.forEach(commit -> result.add(compressCommit(commit)));
        return result;
    }
}
