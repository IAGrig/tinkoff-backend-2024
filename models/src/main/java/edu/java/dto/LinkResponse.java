package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class LinkResponse {
    @JsonProperty("id")
    @Positive
    private Long id;

    @JsonProperty("url")
    @NotBlank
    private String url;

    public LinkResponse id(Long id) {
        this.id = id;
        return this;
    }

    public LinkResponse url(String url) {
        this.url = url;
        return this;
    }
}
