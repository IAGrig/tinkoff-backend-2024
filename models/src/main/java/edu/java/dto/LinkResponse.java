package edu.java.dto;

import edu.java.validators.ValidUri;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class LinkResponse {
    @Positive
    private Long id;

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
