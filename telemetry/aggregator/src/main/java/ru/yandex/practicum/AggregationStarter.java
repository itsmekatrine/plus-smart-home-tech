package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);

    private final ConsumerFactory<String, SensorEventAvro> consumerFactory;
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final SensorEventUpdater sensorEventUpdater;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    @Value("${aggregator.topics.input}")
    private String inputTopic;

    @Value("${aggregator.topics.output}")
    private String outputTopic;


    public void start() {
        try (Consumer<String, SensorEventAvro> consumer = consumerFactory.createConsumer()) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(inputTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(POLL_TIMEOUT);
                int i = 0;

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    sensorEventUpdater.updateState(event).ifPresent(snapshot -> {
                        kafkaTemplate.send(
                                outputTopic,
                                null,
                                snapshot.getTimestamp().toEpochMilli(),
                                snapshot.getHubId(),
                                snapshot
                        );
                        log.info("Snapshot updated for hub {}", snapshot.getHubId());
                    });

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );

                    if ((++i % 10) == 0) {
                        consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                            if (ex != null) {
                                log.warn("Commit async failed: {}", offsets, ex);
                            }
                        });
                    }
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error while processing events", e);
        } finally {
            try (Consumer<String, SensorEventAvro> consumer = consumerFactory.createConsumer()) {
                consumer.commitSync(currentOffsets);
            } catch (Exception e) {
                log.warn("CommitSync on shutdown failed", e);
            }
        }
    }
}