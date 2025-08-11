package org.poc.apigateway.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poc.apigateway.entities.Branch;
import org.poc.apigateway.entities.Commit;
import org.poc.apigateway.entities.Member;
import org.poc.apigateway.entities.MergeRequest;
import org.poc.apigateway.gitlab_api_client.GitLabApiClientService;
import org.poc.apigateway.gitlab_api_client.matcher.CommitAuthorMatcher;
import org.poc.apigateway.gitlab_api_client.matcher.FileCommitAuthorMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
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
    public Member getMemberByEmail(String email) {
        List<Member> members = getMembers();
        for (Member member : members) {
            if (member.email().equals(email)) {
                return member;
            }
        }
        throw new IllegalArgumentException("Member with email " + email + " not found");
    }
    public List<MergeRequest> getMRsAll() {
        JsonNode mrsAll = parseJson(gitLabApiClientService.getMRsAll()),
                membersAll = parseJson(gitLabApiClientService.getMembers()),
                branchesAll = parseJson(gitLabApiClientService.getBranchesAll());

        List<MergeRequest> result = new ArrayList<>();

        mrsAll.forEach(mrNode -> {
            long authorId = mrNode.get("authorId").asLong();
            int iid = mrNode.get("iid").asInt();
            OffsetDateTime createdAt = OffsetDateTime.parse(mrNode.get("createdAt").asText());
            String authorUrl = "";

            for (JsonNode memberNode : membersAll) {
                if (memberNode.get("id").asLong() == authorId) {
                    authorUrl = memberNode.get("webUrl").asText();
                    break;
                }
            }
            String branchUrl = "";
            for (JsonNode branchNode : branchesAll) {
                if (branchNode.get("name").asText().equals(mrNode.get("targetBranch").asText())) {
                    branchUrl = branchNode.get("webUrl").asText();
                    break;
                }
            }

            JsonNode commitsNode = parseJson(gitLabApiClientService.getCommitsByMergeRequestIid(iid));
            OffsetDateTime firstCommitDate = OffsetDateTime.parse(commitsNode.get(commitsNode.size() - 1).get("createdAt").asText());
            Duration timeTaken = Duration.between(firstCommitDate, createdAt);

            result.add(new MergeRequest(
                    mrNode.get("id").asLong(),
                    iid,
                    fileCommitAuthorMatcher.matchId(authorId),
                    authorUrl,
                    mrNode.get("title").asText(),
                    mrNode.get("state").asText(),
                    mrNode.get("targetBranch").asText(),
                    branchUrl,
                    createdAt,
                    OffsetDateTime.parse(mrNode.get("mergedAt").asText()),
                    commitsNode.size(),
                    timeTaken,
                    mrNode.get("webUrl").asText()
            ));
        });
        return result;
    }
    public MergeRequest getMRByIid(long iid){
        List<MergeRequest> mrs = getMRsAll();
        for (MergeRequest mr : mrs) {
            if (mr.iid() == iid) return mr;
        }
        throw new IllegalArgumentException("Merge request with iid " + iid + " not found");
    }
    public List<MergeRequest> getMRsByAuthorEmail(List<MergeRequest> mergeRequests, String authorEmail) {
        List<MergeRequest> result = new LinkedList<>(mergeRequests);
        result.removeIf(mergeRequest -> !mergeRequest.authorEmail().equals(authorEmail));
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Merge request with author email " + authorEmail + " not found");
        }
        return result;
    }
    public List<MergeRequest> getMRsByAuthorEmail(String authorEmail){
        return getMRsByAuthorEmail(getMRsAll(), authorEmail);
    }
    public List<MergeRequest> getMRsSinceUntilDateTime(List<MergeRequest> mergeRequests, OffsetDateTime sinceDateTime, OffsetDateTime untilDateTime) {
        List<MergeRequest> result = new LinkedList<>(mergeRequests);

        result.removeIf(mr ->
                (sinceDateTime != null && mr.createdAt().isBefore(sinceDateTime)) ||
                        (untilDateTime != null && mr.createdAt().isAfter(untilDateTime))
        );

        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    "No merge requests found between " + sinceDateTime + " and " + untilDateTime
            );
        }

        return result;
    }

    public List<MergeRequest> getMRsSinceUntilDateTime(OffsetDateTime sinceDateTime, OffsetDateTime untilDateTime){
        return getMRsSinceUntilDateTime(getMRsAll(), sinceDateTime, untilDateTime);
    }
    public List<MergeRequest> getMRsSinceDateTime(List<MergeRequest> mergeRequests, OffsetDateTime sinceDateTime){
        return getMRsSinceUntilDateTime(mergeRequests, sinceDateTime, null);
    }
    public List<MergeRequest> getMRsSinceDateTime(OffsetDateTime sinceDateTime){
        return getMRsSinceDateTime(getMRsAll(), sinceDateTime);
    }
    public List<MergeRequest> getMRsUntilDateTime(List<MergeRequest> mergeRequests, OffsetDateTime untilDateTime){
        return getMRsSinceUntilDateTime(mergeRequests, null, untilDateTime);
    }
    public List<MergeRequest> getMRsUntilDateTime(OffsetDateTime untilDateTime){
        return getMRsUntilDateTime(getMRsAll(), untilDateTime);
    }
    public List<Branch> getBranchesAll() {
        JsonNode branchesAll = parseJson(gitLabApiClientService.getBranchesAll()),
                membersAll = parseJson(gitLabApiClientService.getMembers());
        List<Branch> result = new ArrayList<>();
        for (JsonNode branchNode : branchesAll) {
            String name = branchNode.get("name").asText();
            JsonNode commitsByBranchName = parseJson(gitLabApiClientService.getCommitsByBranchName(name));
            String lastCommitSha = commitsByBranchName.get(0).get("shortId").asText();
            String lastCommitUrl = commitsByBranchName.get(0).get("webUrl").asText();
            String webUrl = branchNode.get("webUrl").asText();

            result.add(new Branch(
                    name,
                    lastCommitSha,
                    lastCommitUrl,
                    webUrl
            ));
        }
        return result;
    }
    public Branch getBranchByName(String name){
        List<Branch> branches = getBranchesAll();
        for (Branch branch : branches) {
            if (branch.name().equals(name)) return branch;
        }
        throw new IllegalArgumentException("Branch with name " + name + " not found");
    }
    private List<Commit> getCommits(JsonNode commitsNode) {
        JsonNode membersAll = parseJson(gitLabApiClientService.getMembers());
        List<Commit> result = new ArrayList<>();
        for (int i = 0; i < commitsNode.size(); i++) {
            JsonNode commitNode = commitsNode.get(i) ;
            String sha = commitNode.get("shortId").asText();
            String authorEmail = commitNode.get("author").asText();
            String uauthorUrl = "";
            for (JsonNode memberNode : membersAll) {
                if (memberNode.get("id").asLong() == fileCommitAuthorMatcher.matchEmail(authorEmail)) {
                    uauthorUrl = memberNode.get("webUrl").asText();
                    break;
                }
            }
            String message = commitNode.get("message").asText();
            Duration timeTaken = Duration.between(
                    OffsetDateTime.parse(commitsNode.get(i == commitsNode.size() - 1 ? i : i + 1).get("createdAt").asText()),
                    OffsetDateTime.parse(commitNode.get("createdAt").asText())
            );
            OffsetDateTime commitDate = OffsetDateTime.parse(commitNode.get("createdAt").asText());
            String webUrl = commitNode.get("webUrl").asText();

            result.add(new Commit(
                    sha,
                    authorEmail,
                    uauthorUrl,
                    message,
                    timeTaken,
                    commitDate,
                    webUrl
            ));
        }
        return result;
    }
    public List<Commit> getCommitsByMRIid(long iid) {
        return getCommits(parseJson(gitLabApiClientService.getCommitsByMergeRequestIid(iid)));
    }
    public List<Commit> getCommitByBranchName(String branchName){
        return getCommits(parseJson(gitLabApiClientService.getCommitsByBranchName(branchName)));
    }
    public List<Commit> getCommitsByAuthor(List<Commit> commits, String authorEmail){
        List<Commit> result = new LinkedList<>(commits);
        result.removeIf(commit -> !commit.authorEmail().equals(authorEmail));
        if (result.isEmpty()) {
            throw new IllegalArgumentException("No commits found by author with email " + authorEmail);
        }
        return result;
    }
}
