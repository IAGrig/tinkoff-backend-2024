package edu.java.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.scrapper.dto.Response;
import java.util.List;
import lombok.Getter;

@Getter
public class StackoverflowQuestionUpdatesResponse extends Response {
    @JsonProperty("items")
    private List<StackoverflowQuestionUpdatesItemResponse> items;

    public static StackoverflowQuestionUpdatesResponse getEmpty() {
        return new StackoverflowQuestionUpdatesResponse();
    }
}
