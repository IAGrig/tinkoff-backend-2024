package edu.java.dto;

import edu.java.validators.ValidUri;
import java.net.URI;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class AddLinkRequest {
    @ValidUri
    private URI link;

    public AddLinkRequest link(String link) {
        this.link = URI.create(link);
        return this;
    }

    public AddLinkRequest link(URI link) {
        this.link = link;
        return this;
    }
}
