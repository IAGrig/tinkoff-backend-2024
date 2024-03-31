package edu.java.httpClients;

import edu.database.entities.Link;
import edu.java.dto.LinkUpdateRequest;
import edu.java.httpClients.github.GithubHttpClient;
import edu.java.httpClients.stackoverflow.StackoverflowHttpClient;
import edu.java.httpClients.utils.GithubLinkChecker;
import edu.java.httpClients.utils.StackoverflowLinkChecker;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    private final GithubLinkChecker githubLinkChecker;
    private final StackoverflowLinkChecker stackoverflowLinkChecker;
    @Value("${app.scheduler.old-links-hour-period:1}")
    private int hoursCheckPeriod;

    @Autowired
    public LinkUpdateScheduler(
        @Qualifier("jdbcLinkService") LinkService linkService,
        @Qualifier("jdbcUserService") UserService userService,
        HttpClient stackoverflowHttpClient,
        HttpClient githubHttpClient,
        BotHttpClient botHttpClient,
        GithubLinkChecker githubLinkChecker,
        StackoverflowLinkChecker stackoverflowLinkChecker
    ) {
        this.linkService = linkService;
        this.userService = userService;
        this.stackoverflowHttpClient = (StackoverflowHttpClient) stackoverflowHttpClient;
        this.githubHttpClient = (GithubHttpClient) githubHttpClient;
        this.botHttpClient = botHttpClient;
        this.githubLinkChecker = githubLinkChecker;
        this.stackoverflowLinkChecker = stackoverflowLinkChecker;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        List<Link> linksToBeChecked = linkService.getOldCheckedLinks(hoursCheckPeriod);
        List<LinkUpdateRequest> requests = new ArrayList<>();
        log.info("Check links: %s".formatted(linksToBeChecked.stream()
            .map(l -> l.id().toString())
            .collect(Collectors.joining(","))));
        for (Link link : linksToBeChecked) {
            linkService.updateLastCheckTime(link.url());
            switch (link.domain()) {
                // it's better to delegate id parsing to the http client classes
                case "github.com":
                    LinkUpdateRequest githubUpdateRequest = githubLinkChecker.checkLink(link);
                    if (githubUpdateRequest != null) {
                        requests.add(githubUpdateRequest);
                    }
                    break;
                case "stackoverflow.com":
                    LinkUpdateRequest stackoverflowUpdateRequest = stackoverflowLinkChecker.checkLink(link);
                    if (stackoverflowUpdateRequest != null) {
                        requests.add(stackoverflowUpdateRequest);
                    }
                    break;
                default:
            }
        }
        for (LinkUpdateRequest request : requests) {
            botHttpClient.update(request);
        }
    }
}
