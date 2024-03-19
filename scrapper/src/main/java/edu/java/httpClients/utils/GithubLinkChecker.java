package edu.java.httpClients.utils;

import edu.database.entities.Link;
import edu.java.dto.LinkUpdateRequest;
import edu.java.httpClients.dto.github.GithubRepositoryResponse;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GithubLinkChecker {
    public static LinkUpdateRequest checkLink(
        Link link,
        GithubHttpClient githubHttpClient,
        LinkService linkService,
        UserService userService
    ) {
        String repositoryOwner;
        String repository;
        try {
            String ownerRepositoryPart = link.url().split("github.com/")[1];
            repositoryOwner = ownerRepositoryPart.split("/")[0];
            repository = ownerRepositoryPart.split("/")[1];
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        GithubRepositoryResponse githubResponse =
            githubHttpClient.getLastUpdate(repositoryOwner, repository);
        if (link.lastUpdate() == null
            || githubResponse.getUpdatedAt().isAfter(link.lastUpdate())
            || githubResponse.getPushedAt().isAfter(link.lastUpdate())) {
            List<Long> tgChatIds = userService.getUsersIdsWithLink(link);
            OffsetDateTime lastUpdate = githubResponse.getUpdatedAt().isAfter(githubResponse.getPushedAt())
                ? githubResponse.getUpdatedAt()
                : githubResponse.getPushedAt();
            linkService.updateLastUpdateTime(link.url(), lastUpdate);
            return new LinkUpdateRequest().url(link.url()).tgChatIds(tgChatIds);
        }
        return null;
    }
}
