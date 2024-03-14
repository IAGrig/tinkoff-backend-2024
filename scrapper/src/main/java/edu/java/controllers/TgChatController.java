package edu.java.controllers;

import edu.java.services.UserService;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/tg-chat")
public class TgChatController {
    private UserService userService;

    @PostMapping("/{id}")
    public ResponseEntity<String> registerChat(@PathVariable("id") @Positive Long chatId) {
        userService.registerUser(chatId);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable("id") @Positive Long chatId) {
        userService.deleteUser(chatId);
        return ResponseEntity.ok("Чат успешно удалён");
    }
}
