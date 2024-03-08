package edu.java.services;

import edu.database.Database;
import edu.database.entities.Link;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LinkService {
    private Database database;

    public ListLinksResponse getTrackedLinks(Long userId) {
        List<Link> links = database.getAllUserLinks(userId);
        List<LinkResponse> linkResponses = links.stream().map(this::linkToLinkResponse).toList();
        return new ListLinksResponse().links(linkResponses).size(linkResponses.size());
    }

    public LinkResponse addLinkTracking(Long userId, AddLinkRequest request) {
        String domain;
        try {
            domain = request.getLink().getHost();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Link parsing error: %s".formatted(e.getMessage()));
        }

        Long newLinkId = database.createLink(domain, request.getLink().toString());
        database.addLinkToUser(userId, newLinkId);
        return new LinkResponse().id(newLinkId).url(request.getLink());
    }

    public LinkResponse deleteLinkTracking(Long userId, RemoveLinkRequest request) {
        Link link = database.getUserLink(userId, request.getLink().toString());
        database.removeLinkFromUser(userId, request.getLink().toString());
        return new LinkResponse().id(link.id()).url(link.url());
    }

    private LinkResponse linkToLinkResponse(Link link) {
        return new LinkResponse().id(link.id()).url(link.url());
    }
}
