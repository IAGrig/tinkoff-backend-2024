package edu.java.httpClients.stackoverflow;

import edu.java.httpClients.HttpClient;
import edu.java.httpClients.dto.stackoverflow.StackoverflowItemsListResponse;
import edu.java.httpClients.dto.stackoverflow.StackoverflowQuestionUpdatesResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

public class StackoverflowHttpClient implements HttpClient {
    private final WebClient client;
    private final String baseUrlDefault;
    private final String questionsPath;
    private final String questionAnswersPath;
    private final String questionCommentsPath;

    public StackoverflowHttpClient(
        WebClient client,
        String baseUrlDefault,
        String questionsPath,
        String questionAnswersPath,
        String questionCommentsPath
    ) {
        this.client = client;
        this.baseUrlDefault = baseUrlDefault;
        this.questionsPath = questionsPath;
        this.questionAnswersPath = questionAnswersPath;
        this.questionCommentsPath = questionCommentsPath;
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

    public StackoverflowItemsListResponse getUpdates(Long id, OffsetDateTime fromDate) {
        StackoverflowItemsListResponse answersResponse = client.get()
            .uri(uriBuilder -> getAnswersCommentsPath(uriBuilder, questionAnswersPath, id.toString(), fromDate))
            .retrieve()
            .bodyToMono(StackoverflowItemsListResponse.class)
            .onErrorReturn(StackoverflowItemsListResponse.getEmpty())
            .block();
        StackoverflowItemsListResponse commentsResponse = client.get()
            .uri(uriBuilder -> getAnswersCommentsPath(uriBuilder, questionCommentsPath, id.toString(), fromDate))
            .retrieve()
            .bodyToMono(StackoverflowItemsListResponse.class)
            .onErrorReturn(StackoverflowItemsListResponse.getEmpty())
            .block();
        if (commentsResponse.getItems() != null) {
            answersResponse.addItems(commentsResponse.getItems());
        }
        return answersResponse;
    }

    private URI getUri(UriBuilder uriBuilder, String path, String idString) {
        return addQueryParams(uriBuilder
            .path(path)
            .pathSegment(idString), null)
            .build();
    }

    private URI getAnswersCommentsPath(UriBuilder builder, String path, String id, OffsetDateTime fromDate) {
        String orderBy = null;
        if (path.equals("/comments")) {
            orderBy = "creation";
        }
        if (fromDate != null) {
            String timestampStr = Long.toString(fromDate.toEpochSecond());
            return addQueryParams(builder
                .path(questionsPath)
                .pathSegment(id)
                .path(path)
                .queryParam("fromdate", timestampStr), orderBy)
                .build();
        }
        return addQueryParams(builder
            .path(questionsPath)
            .pathSegment(id)
            .path(path), orderBy)
            .build();
    }

    private UriBuilder addQueryParams(UriBuilder builder, String sort) {
        String sortBy = "activity";
        if (sort != null) {
            sortBy = sort;
        }
        return builder
            .queryParam("order", "desc")
            .queryParam("sort", sortBy)
            .queryParam("site", "stackoverflow");
    }
}
