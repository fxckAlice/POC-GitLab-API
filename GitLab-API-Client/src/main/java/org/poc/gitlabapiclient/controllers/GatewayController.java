package org.poc.gitlabapiclient.controllers;

import org.poc.gitlabapiclient.gitlabapi.GitLabApiService;
import org.poc.gitlabapiclient.gitlabapi.data.ObjectsCompressingService;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Branch;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Commit;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Member;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.MergeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/gateway")
@RestController
public class GatewayController {
    private final ObjectsCompressingService objectsCompressingService;
    private final GitLabApiService gitLabApiService;

    public GatewayController(ObjectsCompressingService objectsCompressingService, GitLabApiService gitLabApiService) {
        this.objectsCompressingService = objectsCompressingService;
        this.gitLabApiService = gitLabApiService;
    }
    @GetMapping("/members/all")
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(objectsCompressingService.compressMembers(gitLabApiService.getMembers()));
    }

    @GetMapping("/merge-requests/all")
    public ResponseEntity<List<MergeRequest>> getAllMRs() {
        return ResponseEntity.ok(objectsCompressingService.compressMergeRequests(gitLabApiService.getAllMergeRequests()));
    }

    @GetMapping("/merge-requests")
    public ResponseEntity<List<MergeRequest>> getMRsByAuthorId(@RequestParam("author_id") long authorId) {
        return ResponseEntity.ok(objectsCompressingService.compressMergeRequests(gitLabApiService.getMergeRequestsByAuthorId(authorId)));
    }

    @GetMapping("/branches/all")
    public ResponseEntity<List<Branch>> getBranches() {
        return ResponseEntity.ok(objectsCompressingService.compressBranches(gitLabApiService.getBranches()));
    }

    @GetMapping("/commits/mr")
    public ResponseEntity<List<Commit>> getCommitsMr(@RequestParam("mr_iid") int mrIid) {
        return ResponseEntity.ok(objectsCompressingService.compressCommits(gitLabApiService.getCommitsByMergeRequestIid(mrIid)));
    }

    @GetMapping("/commits/branch")
    public ResponseEntity<List<Commit>> getCommitsBranch(@RequestParam("branch_name") String branchName) {
        return ResponseEntity.ok(objectsCompressingService.compressCommits(gitLabApiService.getCommitsByBranchName(branchName)));
    }

}
