package edu.java.scrapper.stackoverflow;

import edu.java.scrapper.HttpClient;
import edu.java.scrapper.dto.stackoverflow.StackoverflowQuestionUpdatesResponse;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

@Component
public class StackoverflowHttpClient implements HttpClient {
    private final WebClient client;
    @Value("${scrapper.stackoverflow.baseUrl}")
    private String baseUrlDefault;
    @Value("${scrapper.stackoverflow.questionsPath}")
    private String questionsPath;

    public StackoverflowHttpClient(String baseUrl) {
        client = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    public StackoverflowHttpClient() {
        client = WebClient.builder()
            .baseUrl(baseUrlDefault)
            .build();
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
