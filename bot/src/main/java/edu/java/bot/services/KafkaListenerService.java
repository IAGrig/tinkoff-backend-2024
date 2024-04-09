package edu.java.bot.services;

import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaListenerService {
    private final UpdateService updateService;

    @KafkaListener(topics = "${app.kafka-queue.topic-name:botUpdates}", containerFactory = "listenerContainerFactory")
    public void listen(@Payload LinkUpdateRequest request, Acknowledgment acknowledgment) {
        log.info("Send update about the {} to {}", request.getUrl().toString(), request.getTgChatIds().toString());
        updateService.processUpdateRequest(request);
        acknowledgment.acknowledge();
    }
}
