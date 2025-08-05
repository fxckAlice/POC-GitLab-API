package org.poc.apigateway.controllers.dto;

import java.time.OffsetDateTime;

public record MRFilter(
        String author_email,
        OffsetDateTime since,
        OffsetDateTime until
) {
}
