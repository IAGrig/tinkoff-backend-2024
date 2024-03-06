package edu.java.scrapper.github;

import edu.java.scrapper.HttpClient;
import edu.java.scrapper.dto.github.GithubRepositoryResponse;
import org.springframework.web.reactive.function.client.WebClient;

public class GithubHttpClient implements HttpClient {
    private final WebClient client;
    private final String baseUrlDefault;

    public GithubHttpClient(WebClient client, String baseUrlDefault) {
        this.client = client;
        this.baseUrlDefault = baseUrlDefault;
    }

    @Override
    public GithubRepositoryResponse getLastUpdate(Long id) {
        return client.get()
            .uri(uriBuilder -> uriBuilder
                .path("repositories")
                .pathSegment(id.toString())
                .build())
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class)
            .onErrorReturn(GithubRepositoryResponse.getEmptyResponse())
            .block();
    }

    public GithubRepositoryResponse getLastUpdate(String owner, String repositoryName) {
        return client.get()
            .uri(uriBuilder -> uriBuilder
                .path("repos")
                .pathSegment(owner)
                .pathSegment(repositoryName)
                .build())
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class)
            .onErrorReturn(GithubRepositoryResponse.getEmptyResponse())
            .block();
    }
}
