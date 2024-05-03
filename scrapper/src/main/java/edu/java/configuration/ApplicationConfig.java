package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @Bean
    @NotNull
    Scheduler scheduler,
    AccessType databaseAccessType,
    Boolean useQueue,
    KafkaQueue kafkaQueue,
    Metrics metrics
) {
    public enum AccessType {
        JDBC,
        JPA,
        JOOQ
    }

    public record Scheduler(
        boolean enable,
        @NotNull Duration interval,
        @NotNull Duration forceCheckDelay,
        int oldLinksHourPeriod
    ) {
    }

    public record KafkaQueue(
        String bootstrapServer,
        String topicName,
        Integer partitionsCount,
        Integer replicasCount,
        String acksMode,
        Integer lingerMs,
        Boolean enableIdempotence
    ) {
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
