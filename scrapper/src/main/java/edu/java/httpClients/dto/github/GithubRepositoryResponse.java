package edu.java.httpClients.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.httpClients.dto.Response;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class GithubRepositoryResponse extends Response {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("pushed_at")
    private OffsetDateTime pushedAt;

    public static GithubRepositoryResponse getEmptyResponse() {
        return new GithubRepositoryResponse();
    }
}
