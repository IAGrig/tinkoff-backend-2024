package edu.java.services.jpa;

import edu.database.entities.Link;
import edu.java.exceptions.ApiException;
import edu.java.repositories.jpa.JpaLinkRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JpaLinkService implements LinkService {
    private JpaLinkRepository repository;

    @Override
    public Link addLinkTracking(Long tgChatId, URI url) {
        Link link = repository.findByUrl(url.toString());
        if (link == null) {
            link = repository.save(new Link(null, url.getHost(), url.toString(),
                OffsetDateTime.now(), null, OffsetDateTime.now()));
        }
        try {
            repository.addChatTracking(tgChatId, link.getId());
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException("Opps, error. Maybe you already track this link");
        }
        return link;
    }

    @Override
    public Link removeLinkTracking(Long tgChatId, URI url) {
        Link link = repository.findByUrl(url.toString());
        if (link == null) {
            throw new IllegalArgumentException("Your link is not exist");
        }
        repository.removeLinkTracking(tgChatId, link.getId());
        return link;
    }

    @Override
    public List<Link> getTrackedLinks(Long tgChatId) {
        return repository.findAllUserLinks(tgChatId);
    }

    @Override
    public List<Link> getOldCheckedLinks(int hours) {
        return repository.findAllNotCheckedFor(hours);
    }

    @Override
    @Transactional
    public void updateLastCheckTime(String url) {
        Link link = repository.findByUrl(url);
        link.setLastCheck(OffsetDateTime.now());
    }

    @Override
    @Transactional
    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        Link link = repository.findByUrl(url);
        link.setLastUpdate(datetime);
    }
}
