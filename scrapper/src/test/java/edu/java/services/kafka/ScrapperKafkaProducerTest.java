package edu.java.services.kafka;

import edu.java.dto.LinkUpdateRequest;
import edu.java.services.ScrapperKafkaProducer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@EmbeddedKafka(kraft = false, partitions = 3, topics = "kafkaTest", ports = {9092})
public class ScrapperKafkaProducerTest extends KafkaIntegrationTest {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    @Autowired
    private ScrapperKafkaProducer scrapperKafkaProducer;
    private Consumer<Integer, LinkUpdateRequest> consumer;

    @BeforeEach
    public void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("kafkaTestGroup", "true", embeddedKafka);
        consumerProps.putAll(consumerProps());
        ConsumerFactory<Integer, LinkUpdateRequest> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        consumer.subscribe(Pattern.compile("kafkaTest"));
    }

    @Test
    @DisplayName("update method test")
    public void updateTest() {
        LinkUpdateRequest expected = new LinkUpdateRequest().id(123L).url("http://test.com/kafka").tgChatIds(List.of());

        scrapperKafkaProducer.update(expected);

        ConsumerRecords<Integer, LinkUpdateRequest> replies = KafkaTestUtils.getRecords(consumer);
        assertThat(replies.count()).isGreaterThanOrEqualTo(1);
        assertThat(checkIfContainRequest(replies.records("kafkaTest"), expected)).isEqualTo(true);
    }

    @Test
    @DisplayName("update with null request test")
    public void updateNullTest() {
        assertThatThrownBy(() -> scrapperKafkaProducer.update(null)).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("update with null url test")
    public void updateNullUrlTest() {
        assertThatThrownBy(() -> scrapperKafkaProducer.update(new LinkUpdateRequest().tgChatIds(List.of()))).isExactlyInstanceOf(
            NullPointerException.class);
    }

    @Test
    @DisplayName("update with null tgChats test")
    public void updateNullTgChatsTest() {
        assertThatThrownBy(() -> scrapperKafkaProducer.update(new LinkUpdateRequest().url("http://test.com"))).isExactlyInstanceOf(
            NullPointerException.class);
    }

    private static Boolean checkIfContainRequest(
        Iterable<ConsumerRecord<Integer, LinkUpdateRequest>> records,
        LinkUpdateRequest request
    ) {
        for (ConsumerRecord<Integer, LinkUpdateRequest> record : records) {
            LinkUpdateRequest recordRequest = record.value();
            if (recordRequest.getId().equals(request.getId()) && recordRequest.getUrl().equals(request.getUrl())) {
                return true;
            }
        }
        return false;
    }
}
