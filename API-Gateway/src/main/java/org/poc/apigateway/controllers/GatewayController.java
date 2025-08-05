package org.poc.apigateway.controllers;

import org.poc.apigateway.controllers.dto.CommitsByBranchFilter;
import org.poc.apigateway.controllers.dto.CommitsByMRsIidFilter;
import org.poc.apigateway.controllers.dto.MRFilter;
import org.poc.apigateway.data.ResponseConstructingService;
import org.poc.apigateway.entities.Branch;
import org.poc.apigateway.entities.Commit;
import org.poc.apigateway.entities.Member;
import org.poc.apigateway.entities.MergeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    private final ResponseConstructingService responseConstructingService;

    public GatewayController(ResponseConstructingService responseConstructingService) {
        this.responseConstructingService = responseConstructingService;
    }
    @GetMapping("/members/all")
    public ResponseEntity<List<Member>> getMembersAll() {
        return ResponseEntity.ok(responseConstructingService.getMembers());
    }
    @GetMapping("members/email")
    public ResponseEntity<Member> getMemberByEmail(@RequestParam String email) {
        return ResponseEntity.ok(responseConstructingService.getMemberByEmail(email));
    }
    @GetMapping("/merge-requests/all")
    public ResponseEntity<List<MergeRequest>> getMergeRequestsAll() {
        return ResponseEntity.ok(responseConstructingService.getMRsAll());
    }
    @GetMapping("/merge-requests")
    public ResponseEntity<List<MergeRequest>> getMergeRequestsByFilter(@ModelAttribute MRFilter filter) {
        List<MergeRequest> mrs = responseConstructingService.getMRsAll();
        if (filter.author_email() == null && filter.since() == null && filter.until() == null) throw new IllegalArgumentException("At least one filter parameter must be specified");
        if (filter.author_email() != null) {
            mrs = responseConstructingService.getMRsByAuthorEmail(mrs, filter.author_email());
        }
        if (filter.since() != null) {
            mrs = responseConstructingService.getMRsSinceDateTime(mrs, filter.since());
        }
        if (filter.until() != null) {
            mrs = responseConstructingService.getMRsUntilDateTime(mrs, filter.until());
        }
        return ResponseEntity.ok(mrs);
    }
    @GetMapping("/merge-requests/iid")
    public ResponseEntity<MergeRequest> getMergeRequestByIid(@RequestParam int iid) {
        return ResponseEntity.ok(responseConstructingService.getMRByIid(iid));
    }
    @GetMapping("/branches/all")
    public ResponseEntity<List<Branch>> getBranchesAll() {
        return ResponseEntity.ok(responseConstructingService.getBranchesAll());
    }
    @GetMapping("/branches/name")
    public ResponseEntity<Branch> getBranchByName(@RequestParam String name) {
        return ResponseEntity.ok(responseConstructingService.getBranchByName(name));
    }
    @GetMapping("/commits/branch/all")
    public ResponseEntity<List<Commit>> getCommitsByBranchName(@RequestParam String branch_name) {
        if (branch_name == null) throw new IllegalArgumentException("Branch name must be specified");
        return ResponseEntity.ok(responseConstructingService.getCommitByBranchName(branch_name));
    }
    @GetMapping("/commits/branch")
    public ResponseEntity<List<Commit>> getCommitsByBranchNameAndFilter(@ModelAttribute CommitsByBranchFilter filter) {
        if (filter.branch_name() == null || filter.author_email() == null) throw new IllegalArgumentException("Both branch_name and author_email must be specified");
        return ResponseEntity.ok(responseConstructingService.getCommitsByAuthor(responseConstructingService.getCommitByBranchName(filter.branch_name()), filter.author_email()));
    }
    @GetMapping("/commits/mr/all")
    public ResponseEntity<List<Commit>> getCommitsByMRsIid(@RequestParam int mr_iid) {
        if (mr_iid <= 0) throw new IllegalArgumentException("Merge request IID must be positive");
        return ResponseEntity.ok(responseConstructingService.getCommitsByMRIid(mr_iid));
    }
    @GetMapping("/commits/mr")
    public ResponseEntity<List<Commit>> getCommitsByMRsIidAndFilter(@ModelAttribute CommitsByMRsIidFilter filter) {
        if (filter.author_email() == null || filter.iid() <= 0) throw new IllegalArgumentException("Both author_email and IID must be specified");
        return ResponseEntity.ok(responseConstructingService.getCommitsByAuthor(responseConstructingService.getCommitsByMRIid(filter.iid()), filter.author_email()));
    }
}
