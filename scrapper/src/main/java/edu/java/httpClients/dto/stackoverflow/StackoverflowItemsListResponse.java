package edu.java.httpClients.dto.stackoverflow;

import java.util.List;
import lombok.Getter;

@Getter
public class StackoverflowItemsListResponse {
    private List<StackoverflowListItem> items;

    public static StackoverflowItemsListResponse getEmpty() {
        return new StackoverflowItemsListResponse();
    }

    public void addItems(List<StackoverflowListItem> items) {
        this.items.addAll(items);
    }
}
