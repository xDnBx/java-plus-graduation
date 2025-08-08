package ru.practicum.kafka;

import deserializer.EventSimilarityDeserializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConsumerSimilarityService implements AutoCloseable {
    final KafkaConsumer<Long, SpecificRecordBase> consumer;

    public ConsumerSimilarityService(@Value("${kafka.bootstrap-servers}") String bootstrapServers,
                                     @Value("${kafka.group-id.similarity}") String groupId,
                                     @Value("${kafka.auto-commit}") String autoCommit) {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class.getName());

        this.consumer = new KafkaConsumer<>(config);
    }

    public ConsumerRecords<Long, SpecificRecordBase> poll(Duration duration) {
        return consumer.poll(duration);
    }

    public void subscribe(List<String> topics) {
        consumer.subscribe(topics);
    }

    public void commitSync() {
        consumer.commitSync();
    }

    public void wakeup() {
        consumer.wakeup();
    }

    @Override
    public void close() {
        consumer.close();
    }
}