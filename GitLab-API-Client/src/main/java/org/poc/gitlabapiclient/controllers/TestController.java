package org.poc.gitlabapiclient.controllers;

import org.poc.gitlabapiclient.gitlabapi.GitLabApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    private final GitLabApiService gitLabApiService;

    public TestController(GitLabApiService gitLabApiService) {
        this.gitLabApiService = gitLabApiService;
    }
    @GetMapping("/test/members")
    public ResponseEntity<String> getMembers() {
        return ResponseEntity.ok(gitLabApiService.getMembers());
    }

    @GetMapping("/test/mr/all")
    public ResponseEntity<String> getMergeRequestsAll() {
        return ResponseEntity.ok(gitLabApiService.getAllMergeRequests());
    }
    @GetMapping("/test/mr/author")
    public ResponseEntity<String> getMergeRequestsByAuthorId() {
        return ResponseEntity.ok(gitLabApiService.getMergeRequestsByAuthorId(6452232));
    }
    @GetMapping("/test/branches")
    public ResponseEntity<String> getBranches() {
        return ResponseEntity.ok(gitLabApiService.getBranches());
    }
    @GetMapping("/test/commits/mr")
    public ResponseEntity<String> getCommitsMr() {
        return ResponseEntity.ok(gitLabApiService.getCommitsByMergeRequestIid(12));
    }
    @GetMapping("test/commits/branch")
    public ResponseEntity<String> getCommitsBranch() {
        return ResponseEntity.ok(gitLabApiService.getCommitsByBranchName("kali-purple"));
    }
}
