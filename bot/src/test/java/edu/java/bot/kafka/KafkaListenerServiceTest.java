package edu.java.bot.kafka;

import edu.java.bot.services.KafkaListenerService;
import edu.java.bot.services.UpdateService;
import edu.java.dto.LinkUpdateRequest;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EmbeddedKafka(kraft = false, partitions = 3, topics = {"kafkaTest", "kafkaTest_dlq"}, ports = {9092})
@TestPropertySource(properties = {
    "app.kafka-queue.topic-name=kafkaTest"
})
public class KafkaListenerServiceTest extends KafkaIntegrationTest {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaListenerService kafkaListenerService;
    @MockBean
    private UpdateService mockUpdateService;
    @Autowired
    private KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate;
    private static String topic = "kafkaTest";

    @BeforeEach
    public void setUp() {
        kafkaListenerService = new KafkaListenerService(mockUpdateService, kafkaTemplate);
    }

    @Test
    @DisplayName("listen method test")
    public void listenTest() {
        LinkUpdateRequest request = new LinkUpdateRequest().url("https://test.com").tgChatIds(List.of(123L));
        kafkaTemplate.send(topic, request).join();

        Mockito.verify(mockUpdateService, Mockito.timeout(5000)).processUpdateRequest(request);
    }

    @Test
    @DisplayName("Sending to DLQ test")
    public void dlqTest() {
        LinkUpdateRequest request = new LinkUpdateRequest();
        Consumer<Integer, LinkUpdateRequest> consumer = getDlqConsumer();
        consumer.subscribe(List.of("kafkaTest_dlq"));

        kafkaTemplate.send("kafkaTest", request).join();
        ConsumerRecords<Integer, LinkUpdateRequest> records = KafkaTestUtils.getRecords(consumer);

        assertThat(records.count()).isEqualTo(1);
    }

    private Consumer<Integer, LinkUpdateRequest> getDlqConsumer() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        return new DefaultKafkaConsumerFactory<Integer, LinkUpdateRequest>(props).createConsumer();
    }
}
