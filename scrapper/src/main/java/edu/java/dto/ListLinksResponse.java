package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class ListLinksResponse {
    @JsonProperty("links")
    @NotNull
    private List<LinkResponse> links;

    @JsonProperty("size")
    @PositiveOrZero
    private Integer size;

    public ListLinksResponse links(List<LinkResponse> links) {
        this.links = links;
        return this;
    }

    public ListLinksResponse addLinksItem(LinkResponse linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<LinkResponse>();
        }
        this.links.add(linksItem);
        return this;
    }

    public ListLinksResponse size(Integer size) {
        this.size = size;
        return this;
    }

    public Integer getSize() {
        return links.size();
    }
}
