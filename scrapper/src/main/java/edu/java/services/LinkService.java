package edu.java.services;

import edu.database.Database;
import edu.database.entities.Link;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import java.net.URI;
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

    public void addLinkTracking(Long userId, AddLinkRequest request) {
        String domain;
        try {
            domain = URI.create(request.getLink()).getHost();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Link parsing error: %s".formatted(e.getMessage()));
        }

        Long newLinkId = database.createLink(domain, request.getLink());
        database.addLinkToUser(userId, newLinkId);
    }

    public void deleteLinkTracking(Long userId, RemoveLinkRequest request) {
        database.removeLinkFromUser(userId, request.getLink());
    }

    private LinkResponse linkToLinkResponse(Link link) {
        return new LinkResponse().id(link.id()).url(link.url());
    }
}
