package ru.practicum.kafka;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import serializer.AvroSerializer;

import java.time.Duration;
import java.util.Properties;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProducerService implements AutoCloseable {
    final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaProducerService(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class.getName());
        this.producer = new KafkaProducer<>(config);
    }

    public void send(EventSimilarityAvro similarity, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                similarity.getTimestamp().toEpochMilli(),
                similarity.getEventA() + "-" + similarity.getEventB(),
                similarity
        );
        producer.send(record);
        producer.flush();
    }

    public void flush() {
        producer.flush();
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(5));
    }
}