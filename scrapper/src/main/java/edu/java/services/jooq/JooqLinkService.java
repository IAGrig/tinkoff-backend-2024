package edu.java.services.jooq;

import edu.database.entities.Link;
import edu.java.repositories.jooq.JooqLinkRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JooqLinkService implements LinkService {
    private final JooqLinkRepository repository;

    @Override
    public Link addLinkTracking(Long tgChatId, URI url) {
        return repository.addLinkToUser(url.getHost(), url.toString(), tgChatId);
    }

    @Override
    public Link removeLinkTracking(Long tgChatId, URI url) {
        return repository.removeLinkFromUser(url.toString(), tgChatId);
    }

    @Override
    public List<Link> getTrackedLinks(Long tgChatId) {
        return repository.findUserLinks(tgChatId);
    }

    @Override
    public List<Link> getOldCheckedLinks(int hours) {
        return repository.findOldCheckedLinks(hours);
    }

    @Override
    public void updateLastCheckTime(String url) {
        repository.updateLastCheckTime(url);
    }

    @Override
    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        repository.updateLastUpdateTime(url, datetime);
    }
}
