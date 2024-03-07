package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class AddLinkRequest {
    @JsonProperty("link")
    @NotBlank
    private String link;

    public AddLinkRequest link(String link) {
        this.link = link;
        return this;
    }
}
