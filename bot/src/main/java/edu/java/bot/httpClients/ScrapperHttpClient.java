package edu.java.bot.httpClients;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.exceptions.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ScrapperHttpClient {
    private final WebClient client;
    private final String baseUrl;
    private final String chatsPath = "/tg-chat";
    private final String linksPath = "/links";
    private final String chatIdHeader = "Tg-Chat-Id";

    public String registerChat(Long id) {
        return client.post()
            .uri(uriBuilder -> uriBuilder
                .path(chatsPath)
                .pathSegment(id.toString())
                .build())
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiException(error.getExceptionMessage())))
            )
            .bodyToMono(String.class)
            .block();
    }

    public String deleteChat(Long id) {
        return client.delete()
            .uri(uriBuilder -> uriBuilder
                .path(chatsPath)
                .pathSegment(id.toString())
                .build())
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiException(error.getExceptionMessage())))
            )
            .bodyToMono(String.class)
            .block();
    }

    public ListLinksResponse getTrackedLinks(Long tgChatId) {
        return client.get()
            .uri(linksPath)
            .header(chatIdHeader, tgChatId.toString())
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiException(error.getExceptionMessage())))
            )
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public LinkResponse trackLink(Long tgChatId, String link) {
        AddLinkRequest request = new AddLinkRequest().link(link);
        return client.post()
            .uri(linksPath)
            .header(chatIdHeader, tgChatId.toString())
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiException(error.getExceptionMessage())))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public LinkResponse untrackLink(Long tgChatId, String link) {
        return client.method(HttpMethod.DELETE)
            .uri(linksPath)
            .header(chatIdHeader, tgChatId.toString())
            .body(BodyInserters.fromValue(link))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new ApiException(error.getExceptionMessage())))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }
}
