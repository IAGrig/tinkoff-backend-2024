package edu.java.bot.controllers;

import edu.java.bot.services.UpdateService;
import edu.java.dto.LinkUpdateRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/updates")
public class UpdatesController {
    private UpdateService updateService;

    @PostMapping
    public ResponseEntity<String> postUpdates(@RequestBody @Valid LinkUpdateRequest request) {
        updateService.processUpdateRequest(request);
        return ResponseEntity.ok("Обновление обработано");
    }
}
