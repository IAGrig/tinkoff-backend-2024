package edu.java.repositories.jdbc;

import edu.database.entities.Link;
import edu.database.exceptions.LinkNotFoundException;
import edu.database.exceptions.UserNotFoundException;
import edu.java.exceptions.ApiException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.repositories.jdbc.LinkMapUtils.linkFromKeyHolder;

@SuppressWarnings("MultipleStringLiterals")
@AllArgsConstructor
@Repository
public class JdbcLinkRepository {
    private JdbcClient jdbcClient;

    public Link createLink(String domain, String url) {
        String request = "INSERT INTO links (url, domain_name) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = jdbcClient.sql(request)
            .param(url)
            .param(domain)
            .update(keyHolder);
        if (affectedRows == 0) {
            throw new ApiException("Link creation failed");
        }
        return linkFromKeyHolder(keyHolder);
    }

    @Transactional
    public Link addLinkToUser(String domain, String url, Long userId) {
        Link link;
        try {
            link = findLinkByUrl(url);
        } catch (LinkNotFoundException ex) {
            link = createLink(domain, url);
        }

        String assignToUserRequest = "INSERT INTO users_links (link_id, user_tg_id) values (?, ?);";
        int affectedRows;
        try {
            affectedRows = jdbcClient.sql(assignToUserRequest)
                .param(link.id())
                .param(userId)
                .update();
        } catch (DuplicateKeyException ex) {
            throw new ApiException("You already track this link");
        } catch (DataIntegrityViolationException ex) {
            throw new UserNotFoundException("User not found, try to register firstly");
        }
        if (affectedRows == 0) {
            throw new ApiException("Link assignment failed");
        }
        return link;
    }

    public Link removeLinkFromUser(String url, Long userId) {
        Link link = findLinkByUrl(url);
        String request = "DELETE FROM users_links WHERE link_id = ? AND user_tg_id = ?;";
        int affectedRows = jdbcClient.sql(request)
            .param(link.id())
            .param(userId)
            .update();
        if (affectedRows == 0) {
            throw new ApiException("Link is not tracked by the current user");
        }
        return link;
    }

    public List<Link> findUserLinks(Long userId) {
        String getLinksIdsRequest = "SELECT link_id FROM users_links WHERE user_tg_id = ?;";
        List<Long> linksIds = jdbcClient.sql(getLinksIdsRequest)
            .param(userId)
            .query()
            .listOfRows().stream()
            .map(map -> Long.parseLong(map.get("link_id").toString()))
            .toList(); // rewrite?

        if (linksIds.isEmpty()) {
            throw new LinkNotFoundException(String.format("Links not found", userId));
        }

        MapSqlParameterSource parameterSource = new MapSqlParameterSource("ids", linksIds);
        String request = "SELECT id, url, domain_name, registered, last_update, last_check FROM links "
            + "WHERE id IN (:ids)";
        return jdbcClient.sql(request)
            .paramSource(parameterSource)
            .query(new LinkMapUtils())
            .list();
    }

    public Link findLinkByUrl(String url) {
        String selectQuery = "SELECT id, url, domain_name, registered, last_update, last_check "
             + "FROM links WHERE url = ?;";
        try {
            return jdbcClient.sql(selectQuery)
                .param(url)
                .query(new LinkMapUtils())
                .single();
        } catch (EmptyResultDataAccessException ex) {
            throw new LinkNotFoundException(String.format("Link with url=%s not found", url));
        }
    }

    public List<Link> findOldCheckedLinks(int hours) {
        String selectQuery = "SELECT id, url, domain_name, registered, last_update, last_check "
            + "FROM links WHERE last_check + interval '%d hours' < now();".formatted(hours);
        return jdbcClient.sql(selectQuery)
            .query(new LinkMapUtils())
            .list();
    }

    public void updateLastCheckTime(String url) {
        String query = "UPDATE links SET last_check = now() WHERE url = ?;";
        jdbcClient.sql(query)
            .param(url)
            .update();
    }

    public void updateLastUpdateTime(String url, OffsetDateTime datetime) {
        String query = "UPDATE links SET last_check = now(), last_update = ? WHERE url = ?;";
        jdbcClient.sql(query)
            .param(datetime)
            .param(url)
            .update();
    }
}
