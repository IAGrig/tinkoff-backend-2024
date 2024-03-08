package edu.java.httpClients.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class StackoverflowQuestionUpdatesItemResponse {
    @JsonProperty("question_id")
    private Long questionId;
    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivity;
}
