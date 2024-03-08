package edu.java.httpClients;

import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkUpdateRequest;
import edu.java.exceptions.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class BotHttpClient {
    private final WebClient client;
    private final String baseUrl;

    public String update(LinkUpdateRequest request) {
        return client.post()
            .uri("/updates")
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
