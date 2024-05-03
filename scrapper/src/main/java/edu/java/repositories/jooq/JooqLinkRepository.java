package edu.java.repositories.jooq;

import edu.database.entities.Link;
import edu.database.exceptions.LinkNotFoundException;
import edu.java.exceptions.ApiException;
import edu.java.scrapper.domain.jooq.tables.Links;
import edu.java.scrapper.domain.jooq.tables.UsersLinks;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JooqLinkRepository {
    private final DSLContext dsl;

    public Link createLink(String domain, String url) {
        return createLink(domain, url, OffsetDateTime.now(), null, OffsetDateTime.now());
    }

    public Link createLink(
        String domain,
        String url,
        OffsetDateTime registered,
        OffsetDateTime lastUpdate,
        OffsetDateTime lastCheck) {
        try {
            return dsl.insertInto(Links.LINKS)
                .set(Links.LINKS.URL, url)
                .set(Links.LINKS.DOMAIN_NAME, domain)
                .set(Links.LINKS.REGISTERED, registered)
                .set(Links.LINKS.LAST_UPDATE, lastUpdate)
                .set(Links.LINKS.LAST_CHECK, lastCheck)
                .returning()
                .fetchOneInto(Link.class);
        } catch (DuplicateKeyException ex) {
            throw new ApiException("The link already exist.");
        }
    }

    public Link addLinkToUser(String domain, String url, Long userId) {
        Link link = findLinkByUrl(url).orElseGet(() -> createLink(domain, url));
        try {
            dsl.insertInto(UsersLinks.USERS_LINKS)
                .set(UsersLinks.USERS_LINKS.USER_TG_ID, userId)
                .set(UsersLinks.USERS_LINKS.LINK_ID, link.getId().intValue())
                .execute();
            return link;
        } catch (IntegrityConstraintViolationException | DataIntegrityViolationException ex) {
            throw new ApiException("The link is already tracked by the user or user doesn't exist.");
        }
    }

    public Link removeLinkFromUser(String url, Long userId) {
        Link link = findLinkByUrl(url)
            .orElseThrow(() -> new LinkNotFoundException("Link with url=%s does not exist".formatted(url)));
        int affectedRows = dsl.deleteFrom(UsersLinks.USERS_LINKS)
                .where(UsersLinks.USERS_LINKS.USER_TG_ID.eq(userId),
                UsersLinks.USERS_LINKS.LINK_ID.eq(link.getId().intValue()))
            .execute();
        if (affectedRows == 0) {
            throw new ApiException("Link is not tracked by the current user");
        }
        return link;
    }

    public List<Link> findUserLinks(Long userId) {
        List<Integer> linksIds = dsl.select(UsersLinks.USERS_LINKS.LINK_ID)
            .from(UsersLinks.USERS_LINKS)
            .where(UsersLinks.USERS_LINKS.USER_TG_ID.eq(userId))
            .fetchInto(Integer.class);

        return dsl.selectFrom(Links.LINKS)
            .where(Links.LINKS.ID.in(linksIds))
            .fetchInto(Link.class);
    }

    public Optional<Link> findLinkByUrl(String url) {
        return dsl.selectFrom(Links.LINKS)
            .where(Links.LINKS.URL.eq(url))
            .fetchOptional()
            .map(linksRecord -> linksRecord.into(Link.class));
    }

    public List<Link> findOldCheckedLinks(int hours) {
        // WHERE last_check < now() - interval '%d hours'
        return dsl.selectFrom(Links.LINKS)
            .where(Links.LINKS.LAST_CHECK.lt(OffsetDateTime.now().minusHours(hours)))
            .fetchInto(Link.class);
    }

    public void updateLastCheckTime(String url) {
        dsl.update(Links.LINKS)
            .set(Links.LINKS.LAST_CHECK, OffsetDateTime.now())
            .where(Links.LINKS.URL.eq(url))
            .execute();
    }

    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        dsl.update(Links.LINKS)
            .set(Links.LINKS.LAST_CHECK, OffsetDateTime.now())
            .set(Links.LINKS.LAST_UPDATE, datetime)
            .where(Links.LINKS.URL.eq(url))
            .execute();
    }

    public void deleteAll() {
        dsl.deleteFrom(Links.LINKS).execute();
    }
}
