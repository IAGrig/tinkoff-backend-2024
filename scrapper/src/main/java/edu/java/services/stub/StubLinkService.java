package edu.java.services.stub;

import edu.database.Database;
import edu.database.entities.Link;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StubLinkService implements LinkService {
    private Database database;

    public List<Link> getTrackedLinks(Long userId) {
        List<Link> links = database.getAllUserLinks(userId);
        return links;
    }

    public Link addLinkTracking(Long userId, URI url) {
        String domain;
        try {
            domain = url.getHost();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Link parsing error: %s".formatted(e.getMessage()));
        }

        Long newLinkId = database.createLink(domain, url.toString());
        database.addLinkToUser(userId, newLinkId);
        return new Link(newLinkId, url.getHost(), url.toString(), null, null, null);
    }

    public Link removeLinkTracking(Long userId, URI url) {
        Link link = database.getUserLink(userId, url.toString());
        database.removeLinkFromUser(userId, url.toString());
        return link;
    }

    @Override
    public List<Link> getOldCheckedLinks(int hours) {
        // please, use real db
        return List.of();
    }

    @Override
    public void updateLastCheckTime(String url) {
        // please, use real db
    }

    @Override
    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        // please, use real db
    }

}
