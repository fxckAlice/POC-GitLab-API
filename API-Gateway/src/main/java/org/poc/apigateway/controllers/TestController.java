package org.poc.apigateway.controllers;

import org.poc.apigateway.data.ResponseConstructingService;
import org.poc.apigateway.entities.MergeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    private final ResponseConstructingService responseConstructingService;

    public TestController(ResponseConstructingService responseConstructingService) {
        this.responseConstructingService = responseConstructingService;
    }
    @GetMapping("/merge-requests/all")
    public ResponseEntity<List<MergeRequest>> getMergeRequestsAll() {
        return ResponseEntity.ok(responseConstructingService.getMRsAll());
    }
}
