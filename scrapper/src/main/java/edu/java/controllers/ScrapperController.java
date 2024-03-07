package edu.java.controllers;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.services.LinkService;
import edu.java.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class ScrapperController {
    private UserService userService;
    private LinkService linkService;

    @PostMapping(path = "/tg-chat/{id}")
    public ResponseEntity<String> registerChat(@PathVariable("id") @Positive Long chatId) {
        userService.registerUser(chatId);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping(path = "/tg-chat/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable("id") @Positive Long chatId) {
        userService.deleteUser(chatId);
        return ResponseEntity.ok("Чат успешно удалён");
    }

    @GetMapping(path = "/links")
    public ResponseEntity<ListLinksResponse> getTrackingLinks(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId
    ) {
        ListLinksResponse response = linkService.getTrackedLinks(chatId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/links")
    public ResponseEntity<String> addLinkTracking(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @Valid @RequestBody AddLinkRequest request
    ) {
        linkService.addLinkTracking(chatId, request);
        return ResponseEntity.ok("Ссылка успешно добавлена");
    }

    @DeleteMapping(path = "/links")
    public ResponseEntity<String> deleteLinkTracking(
        @Positive @RequestHeader("Tg-Chat-Id") Long chatId,
        @Valid @RequestBody RemoveLinkRequest request
    ) {
        linkService.deleteLinkTracking(chatId, request);
        return ResponseEntity.ok("Ссылка успешно убрана");
    }
}
