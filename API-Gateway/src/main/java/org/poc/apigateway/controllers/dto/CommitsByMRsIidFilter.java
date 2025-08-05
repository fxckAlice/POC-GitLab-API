package org.poc.apigateway.controllers.dto;

public record CommitsByMRsIidFilter(
        int iid,
        String author_email
) {
}
