package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotEmpty
    String allowedDomains,
    KafkaQueue kafkaQueue,
    Metrics metrics
) {
    public record KafkaQueue(
            String bootstrapServer,
            String topicName,
            String consumerUpdatesGroupId,
            String autoOffsetReset,
            Boolean enableAutoCommit,
            ContainerProperties.AckMode ackMode,
            Dlq dlq
    ) {
        public record Dlq(
            String postfix,
            Integer partitionsCount,
            Integer replicasCount
        ) {

        }
    }

    public record Metrics(
        ProcessedUpdatesCount processedUpdatesCount
    ) {
        public record ProcessedUpdatesCount(
            String name,
            String description
        ) {
        }
    }
}
