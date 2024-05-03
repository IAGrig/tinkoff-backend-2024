package edu.java.bot.services;

import edu.java.bot.bot.Bot;
import edu.java.dto.LinkUpdateRequest;
import io.micrometer.core.instrument.Counter;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UpdateService {
    private Bot bot;
    private Counter processedUpdatesCounter;

    public void processUpdateRequest(LinkUpdateRequest request) {
        if (request.getUrl() == null) {
            throw new NullPointerException("Link url is null");
        }
        if (request.getTgChatIds().stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("ChatId is null");
        }
        String message = String.format("Новое обновление по вашей ссылке: %s\nПодробнее:\n%s",
            request.getUrl(), request.getDescription()
        );
        for (Long chatId : request.getTgChatIds()) {
            bot.sendMessage(chatId, message);
        }
        processedUpdatesCounter.increment();
    }
}
