package edu.java.httpClients;

import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkUpdateRequest;
import edu.java.exceptions.ApiException;
import edu.java.httpClients.retry.BackOffPolicy;
import edu.java.httpClients.retry.RetryManager;
import edu.java.services.UpdatesHandler;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotHttpClient implements UpdatesHandler {
    private final static int RETRY_DELAY = 2;
    private final static int RETRY_ATTEMPTS = 3;
    private final WebClient client;
    private final String updatesPath;
    private final BackOffPolicy backOffPolicy;
    private final List<Integer> retryCodes;

    public BotHttpClient(WebClient client, String updatesPath, BackOffPolicy backOffPolicy, List<Integer> retryCodes) {
        this.client = client;
        this.updatesPath = updatesPath;
        this.backOffPolicy = backOffPolicy;
        this.retryCodes = retryCodes;
    }

    public String update(LinkUpdateRequest request) {
        return client.post()
            .uri(updatesPath)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiException(
                        Integer.parseInt(error.getCode()), error
                        .getExceptionMessage())))
            )
            .bodyToMono(String.class)
            .retryWhen(RetryManager.getBackoffSpec(backOffPolicy, retryCodes, RETRY_DELAY, RETRY_ATTEMPTS))
            .block();
    }
}
