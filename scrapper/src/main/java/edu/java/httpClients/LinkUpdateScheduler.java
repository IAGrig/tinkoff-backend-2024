package edu.java.httpClients;

import edu.database.entities.Link;
import edu.java.dto.LinkUpdateRequest;
import edu.java.httpClients.dto.github.GithubRepositoryResponse;
import edu.java.httpClients.dto.stackoverflow.StackoverflowQuestionUpdatesItemResponse;
import edu.java.httpClients.dto.stackoverflow.StackoverflowQuestionUpdatesResponse;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdateScheduler {
    private final LinkService linkService;
    private final UserService userService;
    private final StackoverflowHttpClient stackoverflowHttpClient;
    private final GithubHttpClient githubHttpClient;
    private final BotHttpClient botHttpClient;
    @Value("${app.scheduler.old-links-hour-period:1}")
    private int hoursCheckPeriod;

    @Autowired
    public LinkUpdateScheduler(
        @Qualifier("jdbcLinkService") LinkService linkService,
        @Qualifier("jdbcUserService") UserService userService,
        HttpClient stackoverflowHttpClient,
        HttpClient githubHttpClient,
        BotHttpClient botHttpClient
    ) {
        this.linkService = linkService;
        this.userService = userService;
        this.stackoverflowHttpClient = (StackoverflowHttpClient) stackoverflowHttpClient;
        this.githubHttpClient = (GithubHttpClient) githubHttpClient;
        this.botHttpClient = botHttpClient;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        List<Link> linksToBeChecked = linkService.getOldCheckedLinks(hoursCheckPeriod);
        List<LinkUpdateRequest> requests = new ArrayList<>();
        for (Link link : linksToBeChecked) {
            linkService.updateLastCheckTime(link.url());
            switch (link.domain()) {
                // it's better to delegate id parsing to the http client classes
                case "github.com":
                    String owner;
                    String repository;
                    try {
                        String ownerRepositoryPart = link.url().split("github.com/")[1];
                        owner = ownerRepositoryPart.split("/")[0];
                        repository = ownerRepositoryPart.split("/")[1];
                    } catch (IndexOutOfBoundsException ex) {
                        continue;
                    }
                    GithubRepositoryResponse githubResponse = githubHttpClient.getLastUpdate(owner, repository);
                    if (link.lastUpdate() == null
                        || githubResponse.getUpdatedAt().isAfter(link.lastUpdate())
                        || githubResponse.getPushedAt().isAfter(link.lastUpdate())) {
                        List<Long> tgChatIds = userService.getUsersIdsWithLink(link);
                        OffsetDateTime laterUpdate = githubResponse.getUpdatedAt().isAfter(githubResponse.getPushedAt())
                            ? githubResponse.getUpdatedAt()
                            : githubResponse.getPushedAt();
                        linkService.updateLastUpdateTime(link.url(), laterUpdate);
                        requests.add(new LinkUpdateRequest().url(link.url()).tgChatIds(tgChatIds));
                    }
                    break;
                case "stackoverwlow.com":
                    Long id;
                    try {
                        id = Long.parseLong(link.url().split("stackoverflow.com/questions/")[1]
                            .split("/")[0]);
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        continue;
                    }
                    StackoverflowQuestionUpdatesResponse stackoverflowResponse =
                        stackoverflowHttpClient.getLastUpdate(id);
                    for (StackoverflowQuestionUpdatesItemResponse item : stackoverflowResponse.getItems()) {
                        if (link.lastUpdate() == null || item.getLastActivity().isAfter(link.lastUpdate())) {
                            List<Long> tgChatIds = userService.getUsersIdsWithLink(link);
                            linkService.updateLastUpdateTime(link.url(), item.getLastActivity());
                            requests.add(new LinkUpdateRequest().url(link.url()).tgChatIds(tgChatIds));
                        }
                    }
                    break;
                default: continue;
            }
        }
        for (LinkUpdateRequest request : requests) {
            botHttpClient.update(request);
        }
    }
}
