package edu.java.bot.services;

import edu.java.bot.bot.Bot;
import edu.java.dto.LinkUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UpdateService {
    private Bot bot;

    public void processUpdateRequest(LinkUpdateRequest request) {
        String message = String.format("Новое обновление по вашей ссылке: %s\nПодробнее:\n%s",
            request.getUrl(), request.getDescription()
        );
        for (Long chatId : request.getTgChatIds()) {
            bot.sendMessage(chatId, message);
        }
    }
}
