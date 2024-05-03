package edu.java.configuration;

import edu.java.dto.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@RequiredArgsConstructor
@Configuration
public class KafkaConfiguration {
    private final ApplicationConfig applicationConfig;

    @Bean
    public NewTopic botUpdatesTopic() {
        return TopicBuilder.name(applicationConfig.kafkaQueue().topicName())
            .partitions(applicationConfig.kafkaQueue().partitionsCount())
            .replicas(applicationConfig.kafkaQueue().replicasCount())
            .build();
    }

    @Bean
    public ProducerFactory<Integer, LinkUpdateRequest> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafkaQueue().bootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, applicationConfig.kafkaQueue().acksMode());
        props.put(ProducerConfig.LINGER_MS_CONFIG, applicationConfig.kafkaQueue().lingerMs());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, applicationConfig.kafkaQueue().enableIdempotence());
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate(
        ProducerFactory<Integer, LinkUpdateRequest> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
