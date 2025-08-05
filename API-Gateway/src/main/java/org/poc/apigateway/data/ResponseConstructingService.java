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
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Iterator;
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
    @SuppressWarnings("all")
    public Member getMemberByEmail(String email) {
        List<Member> members = getMembers();
        Iterator<Member> iterator = members.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
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
        Iterator<MergeRequest> iterator = mrs.iterator();
        while (iterator.hasNext()){
            if(iterator.next().iid() == iid) return iterator.next();
        }
        throw new IllegalArgumentException("Merge request with iid " + iid + " not found");
    }
    @SuppressWarnings("all")
    public List<MergeRequest> getMRsByAuthorId(List<MergeRequest> mergeRequests, long authorId) {
        List<MergeRequest> result = new LinkedList<>(mergeRequests);
        Iterator<MergeRequest> iterator = result.iterator();
        while (iterator.hasNext()){
            if(iterator.next().id() == authorId) iterator.remove();
        }
        return result;
    }
    public List<MergeRequest> getMRsByAuthorId(long authorId){
        return getMRsByAuthorId(getMRsAll(), authorId);
    }
    @SuppressWarnings("all")
    public List<MergeRequest> getMRsSinceUntilDateTime(List<MergeRequest> mergeRequests, OffsetDateTime sinceDateTime, OffsetDateTime untilDateTime){
        List<MergeRequest> result = new LinkedList<>(mergeRequests);
        Iterator<MergeRequest> iterator = result.iterator();
        while (iterator.hasNext()){
            MergeRequest mr = iterator.next();
            if(sinceDateTime != null && !mr.createdAt().isBefore(sinceDateTime) && untilDateTime != null && mr.createdAt().isAfter(untilDateTime)) iterator.remove();
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
    public List<MergeRequest> getMRsSinceFirstOfTheMonth(List<MergeRequest> mergeRequests){
        OffsetDateTime firstOfTheMonth = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay()
                .atOffset(ZoneOffset.of("+02:00"));
        return getMRsSinceDateTime(mergeRequests, firstOfTheMonth);
    }
    public List<MergeRequest> getMRsSinceFirstOfTheMonth(){
        return getMRsSinceFirstOfTheMonth(getMRsAll());
    }
    public List<MergeRequest> getMRsSinceLastMonday(List<MergeRequest> mergeRequests){
        return getMRsSinceDateTime(mergeRequests, LocalDate
                .now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay()
                .atOffset(ZoneOffset.of("+02:00"))
        );
    }
    public List<MergeRequest> getMRsSinceLastMonday(){
        return getMRsSinceLastMonday(getMRsAll());
    }
    @SuppressWarnings("all")
    public List<Branch> getBranchesAll() {
        JsonNode branchesAll = parseJson(gitLabApiClientService.getBranchesAll()),
                membersAll = parseJson(gitLabApiClientService.getMembers());
        List<Branch> result = new ArrayList<>();
        Iterator<JsonNode> iterator = branchesAll.iterator();
        while (iterator.hasNext()) {
            JsonNode branchNode = iterator.next();

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
        Iterator<Branch> iterator = branches.iterator();
        while (iterator.hasNext()){
            if(iterator.next().name().equals(name)) return iterator.next();
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
}
