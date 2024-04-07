package edu.java.bot.services;

import edu.java.dto.LinkUpdateRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class KafkaListenerService {
    @KafkaListener(topics = "${app.kafka-queue.topic-name:botUpdates}", containerFactory = "listenerContainerFactory")
    public void listen(@Payload LinkUpdateRequest request, Acknowledgment acknowledgment) {
        log.info(request);
        acknowledgment.acknowledge();
    }
}
