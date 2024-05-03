package edu.java.controllers;

import edu.database.entities.Link;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.services.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinksController {
    private final LinkService linkService;

    @Autowired
    public LinksController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping
    public ResponseEntity<ListLinksResponse> getTrackingLinks(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId
    ) {
        List<LinkResponse> links = linkService.getTrackedLinks(chatId)
            .stream()
            .map(link -> new LinkResponse().id(link.getId()).url(link.getUrl()))
            .toList();
        return ResponseEntity.ok(new ListLinksResponse().links(links));
    }

    @PostMapping
    public ResponseEntity<LinkResponse> addLinkTracking(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @Valid @RequestBody AddLinkRequest request
    ) {
        Link link = linkService.addLinkTracking(chatId, request.getLink());
        return ResponseEntity.ok(new LinkResponse().id(link.getId()).url(link.getUrl()));
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> deleteLinkTracking(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @Valid @RequestBody RemoveLinkRequest request
    ) {
        Link link = linkService.removeLinkTracking(chatId, request.getLink());
        return ResponseEntity.ok(new LinkResponse().id(link.getId()).url(link.getUrl()));
    }
}
