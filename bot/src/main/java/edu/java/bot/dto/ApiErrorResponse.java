package edu.java.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class ApiErrorResponse {
    @JsonProperty("description")
    private String description;

    @JsonProperty("code")
    @NotBlank
    private String code;

    @JsonProperty("exceptionName")
    @NotBlank
    private String exceptionName;

    @JsonProperty("exceptionMessage")
    private String exceptionMessage;

    @JsonProperty("stacktrace")
    private List<String> stacktrace;

    public ApiErrorResponse description(String description) {
        this.description = description;
        return this;
    }

    public ApiErrorResponse code(String code) {
        this.code = code;
        return this;
    }

    public ApiErrorResponse exceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
        return this;
    }

    public ApiErrorResponse exceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public ApiErrorResponse stacktrace(List<String> stacktrace) {
        this.stacktrace = stacktrace;
        return this;
    }

    public ApiErrorResponse addStacktraceItem(String stacktraceItem) {
        if (this.stacktrace == null) {
            this.stacktrace = new ArrayList<String>();
        }
        this.stacktrace.add(stacktraceItem);
        return this;
    }
}
