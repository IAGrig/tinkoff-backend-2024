package edu.java.scrapper.stackoverflow;

import edu.java.scrapper.HttpClient;
import edu.java.scrapper.dto.stackoverflow.StackoverflowQuestionUpdatesResponse;
import java.net.URI;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

public class StackoverflowHttpClient implements HttpClient {
    private final WebClient client;
    private final String baseUrlDefault;
    private final String questionsPath;

    public StackoverflowHttpClient(WebClient client, String baseUrlDefault, String questionsPath) {
        this.client = client;
        this.baseUrlDefault = baseUrlDefault;
        this.questionsPath = questionsPath;
    }

    @Override
    public StackoverflowQuestionUpdatesResponse getLastUpdate(Long id) {
        return client.get()
            .uri(uriBuilder -> getUri(uriBuilder, questionsPath, id.toString()))
            .retrieve()
            .bodyToMono(StackoverflowQuestionUpdatesResponse.class)
            .onErrorReturn(StackoverflowQuestionUpdatesResponse.getEmpty())
            .block();
    }

    public StackoverflowQuestionUpdatesResponse getLastUpdate(List<Long> ids) {
        String idsList = String.join(";", ids.stream().map(Object::toString).toList());
        return client.get()
            .uri(uriBuilder -> getUri(uriBuilder, questionsPath, idsList))
            .retrieve()
            .bodyToMono(StackoverflowQuestionUpdatesResponse.class)
            .onErrorReturn(StackoverflowQuestionUpdatesResponse.getEmpty())
            .block();
    }

    private URI getUri(UriBuilder uriBuilder, String path, String idString) {
        return uriBuilder
            .path(path)
            .pathSegment(idString)
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .build();
    }
}
