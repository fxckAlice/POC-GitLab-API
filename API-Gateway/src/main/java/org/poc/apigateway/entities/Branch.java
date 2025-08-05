package org.poc.apigateway.entities;

public record Branch (
        String name,
        String lastCommitSha,
        String lastCommitUrl,
        String webUrl
){
}
