package edu.java.scrapper.github;

import edu.java.scrapper.HttpClient;
import edu.java.scrapper.dto.github.GithubRepositoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubHttpClient implements HttpClient {
    private final WebClient client;
    @Value("${scrapper.github.baseUrl}")
    private String baseUrlDefault;

    public GithubHttpClient(String baseUrl) {
        client = getWebClientWithBaseUrl(baseUrl);
    }

    public GithubHttpClient() {
        client = getWebClientWithBaseUrl(baseUrlDefault);
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
            .block();
    }

    private WebClient getWebClientWithBaseUrl(String baseUrl) {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
    }
}
