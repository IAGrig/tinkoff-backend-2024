package edu.java.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class LinkUpdateRequest {
    @JsonProperty("id")
    @Positive
    private Long id;

    @JsonProperty("url")
    @NotEmpty
    private String url;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tgChatIds")
    @NotNull
    private List<Long> tgChatIds;

    public LinkUpdateRequest id(Long id) {
        this.id = id;
        return this;
    }

    public LinkUpdateRequest url(String url) {
        this.url = url;
        return this;
    }

    public LinkUpdateRequest description(String description) {
        this.description = description;
        return this;
    }

    public LinkUpdateRequest tgChatIds(List<Long> tgChatIds) {
        this.tgChatIds = tgChatIds;
        return this;
    }

    public LinkUpdateRequest addTgChatIdsItem(Long tgChatIdsItem) {
        if (this.tgChatIds == null) {
            this.tgChatIds = new ArrayList<Long>();
        }
        this.tgChatIds.add(tgChatIdsItem);
        return this;
    }
}
