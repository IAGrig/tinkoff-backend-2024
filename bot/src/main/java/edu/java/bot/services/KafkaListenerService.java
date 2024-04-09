package edu.java.bot.services;

import edu.java.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaListenerService {
    private final UpdateService updateService;
    @Value("${app.kafka-queue.dlq.postfix:_dlq}")
    private String dlqPostfix;
    private final KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;

    @RetryableTopic(
        attempts = "1",
        dltTopicSuffix = "${app.kafka-queue.dlq.postfix:_dlq}",
        autoCreateTopics = "false", // I create all necessary topics in KafkaConfiguration
        backoff = @Backoff(delay = 2500)
    )
    @KafkaListener(
        topics = "${app.kafka-queue.topic-name:botUpdates}",
        containerFactory = "listenerContainerFactory"
    )
    public void listen(
        @Payload LinkUpdateRequest request,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String receivedTopic,
        Acknowledgment acknowledgment) {
        try {
            updateService.processUpdateRequest(request);
            log.info("The update about the {} was sent to {}", request.getUrl(), request.getTgChatIds().toString());
        } catch (KafkaException | NullPointerException | IllegalStateException ex) {
            log.warn(ex);
            throw ex; // retry (if you have enough attempts) or send to dlq
        } finally {
            acknowledgment.acknowledge();
        }
    }

    @DltHandler
    public void handleError(@Payload LinkUpdateRequest request, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Send to DLQ topic {} request: {}", topic, request);
    }
}
