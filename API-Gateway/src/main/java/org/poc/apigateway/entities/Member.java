package org.poc.apigateway.entities;

public record Member(
        long id,
        String username,
        String email,
        int accessLevel,
        String webUrl,
        int createdMRs
) {
}
