package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.ConsumerActionService;
import ru.practicum.handler.UserActionHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionProcessor implements Runnable {
    final ConsumerActionService consumer;
    final UserActionHandler userActionHandler;

    @Value("${kafka.topics.action}")
    String topic;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("Подписка на топик {}", topic + "...");
            consumer.subscribe(List.of(topic));

            while (true) {
                log.info("Ожидание сообщений...");
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                log.info("Получено {} сообщений", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                        UserActionAvro avro = (UserActionAvro) record.value();
                        log.info("Обработка действия пользователя = {}", avro);
                        userActionHandler.handle(avro);
                        log.info("Действие пользователя = {} обработано", avro);
                    }
                    log.info("Выполнение фиксации смещений");
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки сообщений", e);
        } finally {
            try {
                log.info("Фиксация смещений");
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка во время сброса данных", e);
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}