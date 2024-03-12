package edu.java.httpClients;

import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkUpdateRequest;
import edu.java.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotHttpClient {
    private final WebClient client;
    private final String updatesPath;

    public BotHttpClient(WebClient client, String updatesPath) {
        this.client = client;
        this.updatesPath = updatesPath;
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
            .block();
    }
}
