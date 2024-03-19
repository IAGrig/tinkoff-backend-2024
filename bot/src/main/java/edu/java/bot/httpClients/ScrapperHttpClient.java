package edu.java.bot.httpClients;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.exceptions.ApiException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ScrapperHttpClient {
    private final WebClient client;
    private final String chatsPath;
    private final String linksPath;
    private final String chatIdHeader;

    public ScrapperHttpClient(WebClient client, String chatsPath, String linksPath, String chatIdHeader) {
        this.client = client;
        this.chatsPath = chatsPath;
        this.linksPath = linksPath;
        this.chatIdHeader = chatIdHeader;
    }

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
        RemoveLinkRequest request = new RemoveLinkRequest().link(link);
        return client.method(HttpMethod.DELETE)
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
}
