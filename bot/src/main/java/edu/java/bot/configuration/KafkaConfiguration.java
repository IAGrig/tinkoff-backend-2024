package edu.java.bot.configuration;

import edu.java.dto.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfiguration {
    @Value("${app.kafka-queue.bootstrap-server:localhost:9092}")
    private String bootstrapServer;
    @Value("${app.kafka-queue.consumer-updates-group-id:updates}")
    private String groupId;
    @Value("${app.kafka-queue.auto-offset-reset:latest}")
    private String autoOffsetResetmode;
    @Value("${app.kafka-queue.enable-auto-commit:false}")
    private boolean autoCommit;

    @Bean
    public ConsumerFactory<Integer, LinkUpdateRequest> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetmode);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> listenerContainerFactory(
        ConsumerFactory<Integer, LinkUpdateRequest> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
