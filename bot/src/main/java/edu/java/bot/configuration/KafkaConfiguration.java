package edu.java.bot.configuration;

import edu.java.dto.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@RequiredArgsConstructor
@Configuration
public class KafkaConfiguration {
    private final ApplicationConfig applicationConfig;

    @Bean
    public ConsumerFactory<Integer, LinkUpdateRequest> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafkaQueue().bootstrapServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, applicationConfig.kafkaQueue().consumerUpdatesGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, applicationConfig.kafkaQueue().autoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, applicationConfig.kafkaQueue().enableAutoCommit());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> listenerContainerFactory(
        ConsumerFactory<Integer, LinkUpdateRequest> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(applicationConfig.kafkaQueue().ackMode());
        return factory;
    }
}
