package edu.java.httpClients.github;

import edu.java.httpClients.HttpClient;
import edu.java.httpClients.dto.github.GithubRepositoryResponse;
import edu.java.httpClients.retry.BackOffPolicy;
import edu.java.httpClients.retry.RetryManager;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;

public class GithubHttpClient implements HttpClient {
    private final static int RETRY_DELAY = 2;
    private final static int RETRY_ATTEMPTS = 3;
    private final WebClient client;
    private final String baseUrlDefault;
    private final BackOffPolicy backOffPolicy;
    private final List<Integer> retryCodes;

    public GithubHttpClient(
        WebClient client,
        String baseUrlDefault,
        BackOffPolicy backOffPolicy,
        List<Integer> retryCodes
    ) {
        this.client = client;
        this.baseUrlDefault = baseUrlDefault;
        this.backOffPolicy = backOffPolicy;
        this.retryCodes = retryCodes;
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
            .retryWhen(RetryManager.getBackoffSpec(backOffPolicy, retryCodes, RETRY_DELAY, RETRY_ATTEMPTS))
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
            .retryWhen(RetryManager.getBackoffSpec(backOffPolicy, retryCodes, RETRY_DELAY, RETRY_ATTEMPTS))
            .onErrorReturn(GithubRepositoryResponse.getEmptyResponse())
            .block();
    }
}
