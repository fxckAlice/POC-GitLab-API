package org.poc.apigateway.entities;

import java.time.Duration;
import java.time.OffsetDateTime;

public record Branch (
        String name,
        String lastCommitSha,
        String lastCommitUrl,
        String webUrl
){
}
