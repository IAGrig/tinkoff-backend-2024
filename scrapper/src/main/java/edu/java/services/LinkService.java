package edu.java.services;

import edu.database.entities.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    Link addLinkTracking(Long tgChatId, URI url);

    Link removeLinkTracking(Long tgChatId, URI url);

    List<Link> getTrackedLinks(Long tgChatId);

    List<Link> getOldCheckedLinks(int hours);

    void updateLastCheckTime(String url);

    void updateLastUpdateTime(String url, OffsetDateTime datetime);
}
