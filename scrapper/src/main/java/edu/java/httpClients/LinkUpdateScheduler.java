package edu.java.httpClients;

import edu.database.entities.Link;
import edu.java.dto.LinkUpdateRequest;
import edu.java.httpClients.utils.GithubLinkChecker;
import edu.java.httpClients.utils.StackoverflowLinkChecker;
import edu.java.services.LinkService;
import edu.java.services.UpdatesHandler;
import io.micrometer.core.instrument.Counter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdateScheduler {
    private final LinkService linkService;
    private final GithubLinkChecker githubLinkChecker;
    private final StackoverflowLinkChecker stackoverflowLinkChecker;
    private final UpdatesHandler updatesHandler;
    @Value("${app.scheduler.old-links-hour-period:1}")
    private int hoursCheckPeriod;
    private Counter processedUpdatesCounter;

    @Autowired
    public LinkUpdateScheduler(
        LinkService linkService,
        GithubLinkChecker githubLinkChecker,
        StackoverflowLinkChecker stackoverflowLinkChecker,
        UpdatesHandler updatesHandler,
        Counter processedUpdatesCounter
    ) {
        this.linkService = linkService;
        this.githubLinkChecker = githubLinkChecker;
        this.stackoverflowLinkChecker = stackoverflowLinkChecker;
        this.updatesHandler = updatesHandler;
        this.processedUpdatesCounter = processedUpdatesCounter;
    }

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        List<Link> linksToBeChecked = linkService.getOldCheckedLinks(hoursCheckPeriod);
        List<LinkUpdateRequest> requests = new ArrayList<>();
        log.info("Check links: %s".formatted(linksToBeChecked.stream()
            .map(l -> l.getId().toString())
            .collect(Collectors.joining(","))));
        for (Link link : linksToBeChecked) {
            linkService.updateLastCheckTime(link.getUrl());
            switch (link.getDomain()) {
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
        // guarantee of message delivery
        Queue<LinkUpdateRequest> queue = new ArrayDeque<>(requests);
        while (!queue.isEmpty()) {
            LinkUpdateRequest request = queue.poll();
            try {
                updatesHandler.update(request);
                processedUpdatesCounter.increment();
            } catch (Exception ex) { // TODO change exception class
                log.warn(ex);
                queue.add(request);
            }
        }
    }
}
