package edu.java.services;

import edu.java.dto.LinkUpdateRequest;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@ConditionalOnProperty(
    value = "app.useQueue",
    havingValue = "true"
)
@Service
public class ScrapperKafkaProducer implements UpdatesHandler {
    private final KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;
    @Value("${app.kafka-queue.topic-name}")
    private String topicName;

    public String update(LinkUpdateRequest update) {
        CompletableFuture<SendResult<Integer, LinkUpdateRequest>> future = kafkaTemplate.send(topicName, update);
        future.whenComplete(((sendResult, throwable) -> {
            if (throwable == null) {
                ProducerRecord<Integer, LinkUpdateRequest> producerRecord = sendResult.getProducerRecord();
                LinkUpdateRequest request = producerRecord.value();
                String topic = producerRecord.topic();
                Integer partition = producerRecord.partition();
                log.info("Request {} written in {} topic {} partition", request, topic, partition);
            } else {
                log.warn(throwable);
            }
        }));
        return "Update request sent";
    }
}
