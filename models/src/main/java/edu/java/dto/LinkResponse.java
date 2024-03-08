package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.java.validators.ValidUri;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class LinkResponse {
    @JsonProperty("id")
    @Positive
    private Long id;

    @JsonProperty("url")
    @ValidUri
    private URI url;

    public LinkResponse id(Long id) {
        this.id = id;
        return this;
    }

    public LinkResponse url(String url) {
        this.url = URI.create(url);
        return this;
    }

    public LinkResponse url(URI url) {
        this.url = url;
        return this;
    }
}
