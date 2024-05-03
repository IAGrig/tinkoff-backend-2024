package edu.java.httpClients.utils;

import edu.database.entities.Link;
import edu.java.dto.LinkUpdateRequest;
import edu.java.httpClients.HttpClient;
import edu.java.httpClients.dto.stackoverflow.StackoverflowItemsListResponse;
import edu.java.httpClients.dto.stackoverflow.StackoverflowListItem;
import edu.java.httpClients.dto.stackoverflow.StackoverflowOwner;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class StackoverflowLinkChecker {
    private StackoverflowHttpClient stackoverflowHttpClient;
    private LinkService linkService;
    private UserService userService;

    public StackoverflowLinkChecker(HttpClient stackoverflowHttpClient,
        @Qualifier("jdbcLinkService") LinkService linkService,
        @Qualifier("jdbcUserService") UserService userService) {
        this.stackoverflowHttpClient = (StackoverflowHttpClient) stackoverflowHttpClient;
        this.linkService = linkService;
        this.userService = userService;
    }

    public LinkUpdateRequest checkLink(Link link) {
        Long id;
        OffsetDateTime lastUpdateDateTime = OffsetDateTime.MIN;
        try {
            id = Long.parseLong(link.getUrl().split("stackoverflow.com/questions/")[1]
                .split("/")[0]);
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return null;
        }
        List<Long> tgChatIds = userService.getUsersIdsWithLink(link);
        StringBuilder description = new StringBuilder();
        StackoverflowItemsListResponse stackoverflowResponse =
            stackoverflowHttpClient.getUpdates(id, link.getLastUpdate());
        if (stackoverflowResponse.getItems() == null) {
            return null;
        }
        for (StackoverflowListItem item : stackoverflowResponse.getItems()) {
            lastUpdateDateTime = getMaxDateTime(lastUpdateDateTime, item.getCreation_date());
            if (item.getLast_activity_date() != null) {
                lastUpdateDateTime = getMaxDateTime(lastUpdateDateTime, item.getLast_activity_date());
            }
            StackoverflowOwner itemOwner = item.getOwner();
            if (item.getAnswer_id() != null) { // answer type
                description.append("user %s (%d reputation) answered\n"
                    .formatted(itemOwner.getDisplay_name(), itemOwner.getReputation()));
            }
            if (item.getComment_id() != null) { // comment type
                description.append("user %s (%d reputation) commented\n"
                    .formatted(itemOwner.getDisplay_name(), itemOwner.getReputation()));
            }
        }
        if (lastUpdateDateTime.isAfter(OffsetDateTime.MIN)) {
            linkService.updateLastUpdateTime(link.getUrl(), lastUpdateDateTime);
        }
        return new LinkUpdateRequest()
            .url(link.getUrl())
            .tgChatIds(tgChatIds)
            .description(description.toString());
    }

    private static OffsetDateTime getMaxDateTime(OffsetDateTime dt1, OffsetDateTime dt2) {
        return (dt2 == null || dt1.isAfter(dt2)) ? dt1 : dt2;
    }
}
