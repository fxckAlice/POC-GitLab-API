package org.poc.apigateway.controllers.dto;

public record CommitsByBranchFilter(
        String branch_name,
        String author_email
) {
}
