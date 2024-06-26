package edu.java.dto;

import edu.java.validators.ValidUri;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class RemoveLinkRequest {
    @ValidUri
    @NotNull
    private URI link;

    public RemoveLinkRequest link(String link) {
        this.link = URI.create(link);
        return this;
    }

    public RemoveLinkRequest link(URI link) {
        this.link = link;
        return this;
    }
}
