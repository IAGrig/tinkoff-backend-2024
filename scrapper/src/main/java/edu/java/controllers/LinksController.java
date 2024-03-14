package edu.java.controllers;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.services.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/links")
public class LinksController {
    private LinkService linkService;

    @GetMapping
    public ResponseEntity<ListLinksResponse> getTrackingLinks(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId
    ) {
        ListLinksResponse response = linkService.getTrackedLinks(chatId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LinkResponse> addLinkTracking(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @Valid @RequestBody AddLinkRequest request
    ) {
        LinkResponse response = linkService.addLinkTracking(chatId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> deleteLinkTracking(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @Valid @RequestBody RemoveLinkRequest request
    ) {
        LinkResponse response = linkService.deleteLinkTracking(chatId, request);
        return ResponseEntity.ok(response);
    }
}
