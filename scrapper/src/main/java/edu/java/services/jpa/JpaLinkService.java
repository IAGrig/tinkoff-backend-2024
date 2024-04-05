package edu.java.services.jpa;

import edu.database.entities.Link;
import edu.java.exceptions.ApiException;
import edu.java.repositories.jpa.JpaLinkRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JpaLinkService implements LinkService {
    private JpaLinkRepository repository;

    @Override
    public Link addLinkTracking(Long tgChatId, URI url) {
        Link link;
        Optional<Link> optionalLink = repository.findByUrl(url.toString());
        link = optionalLink.orElseGet(() -> repository.save(new Link(null, url.getHost(), url.toString(),
                OffsetDateTime.now(), null, OffsetDateTime.now())));
        try {
            repository.addChatTracking(tgChatId, link.getId());
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException("Opps, error. Maybe you already track this link");
        }
        return link;
    }

    @Override
    public Link removeLinkTracking(Long tgChatId, URI url) {
        Optional<Link> link = repository.findByUrl(url.toString());
        if (link.isEmpty()) {
            throw new IllegalArgumentException("Your link is not exist");
        }
        repository.removeLinkTracking(tgChatId, link.get().getId());
        return link.get();
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
        Optional<Link> link = repository.findByUrl(url);
        if (link.isEmpty()) {
            return;
        }
        link.get().setLastCheck(OffsetDateTime.now());
    }

    @Override
    @Transactional
    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        Optional<Link> link = repository.findByUrl(url);
        if (link.isEmpty()) {
            return;
        }
        link.get().setLastUpdate(datetime);
    }
}
