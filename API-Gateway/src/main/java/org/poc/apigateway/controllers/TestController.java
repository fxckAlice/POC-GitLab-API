package org.poc.apigateway.controllers;

import org.poc.apigateway.data.ResponseConstructingService;
import org.poc.apigateway.entities.Branch;
import org.poc.apigateway.entities.Commit;
import org.poc.apigateway.entities.MergeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gateway")
public class TestController {
    private final ResponseConstructingService responseConstructingService;

    public TestController(ResponseConstructingService responseConstructingService) {
        this.responseConstructingService = responseConstructingService;
    }
    @GetMapping("/merge-requests/all")
    public ResponseEntity<List<MergeRequest>> getMergeRequestsAll() {
        return ResponseEntity.ok(responseConstructingService.getMRsAll());
    }
    @GetMapping("/branches/all")
    public ResponseEntity<List<Branch>> getBranchesAll() {
        return ResponseEntity.ok(responseConstructingService.getBranchesAll());
    }
    @GetMapping("/commits/branch")
    public ResponseEntity<List<Commit>> getCommitsByBranchName(@RequestParam String branch_name) {
        return ResponseEntity.ok(responseConstructingService.getCommitByBranchName(branch_name));
    }
    @GetMapping("/commits/mr")
    public ResponseEntity<List<Commit>> getCommitsByMRsIid(@RequestParam int mr_iid) {
        return ResponseEntity.ok(responseConstructingService.getCommitsByMRIid(mr_iid));
    }
}
