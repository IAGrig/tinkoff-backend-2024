package edu.java.services.jdbc;

import edu.database.entities.Link;
import edu.java.repositories.jdbc.JdbcLinkRepository;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Log4j2
@Service("jdbcLinkService")
public class JdbcLinkService implements LinkService {
    private JdbcLinkRepository linkRepository;

    @Override
    public Link addLinkTracking(Long tgChatId, URI url) {
        return linkRepository.addLinkToUser(url.getHost(), url.toString(), tgChatId);
    }

    @Override
    public Link removeLinkTracking(Long tgChatId, URI url) {
        return linkRepository.removeLinkFromUser(url.toString(), tgChatId);
    }

    @Override
    public List<Link> getTrackedLinks(Long tgChatId) {
        return linkRepository.findUserLinks(tgChatId);
    }

    @Override
    public List<Link> getOldCheckedLinks(int hours) {
        return linkRepository.findOldCheckedLinks(hours);
    }

    @Override
    public void updateLastCheckTime(String url) {
        linkRepository.updateLastCheckTime(url);
    }

    @Override
    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        linkRepository.updateLastUpdateTime(url, datetime);
    }
}
