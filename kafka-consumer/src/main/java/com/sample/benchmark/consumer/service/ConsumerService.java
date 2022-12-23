package com.sample.benchmark.consumer.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sample.benchmark.BenchmarkMessageOuterClass;
import com.sample.benchmark.consumer.document.BenchmarkMessageDocument;
import com.sample.benchmark.consumer.repository.BenchmarkMessageDocumentRepository;
import com.sample.benchmark.consumer.repository.BenchmarkMessageReactiveRepository;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Service
public class ConsumerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    private final BenchmarkMessageDocumentRepository benchmarkMessageDocumentRepository;
    private final BenchmarkMessageReactiveRepository benchmarkMessageReactiveRepository;

    public ConsumerService(BenchmarkMessageDocumentRepository benchmarkMessageDocumentRepository, BenchmarkMessageReactiveRepository benchmarkMessageReactiveRepository) {
        this.benchmarkMessageDocumentRepository = benchmarkMessageDocumentRepository;
        this.benchmarkMessageReactiveRepository = benchmarkMessageReactiveRepository;
    }

    @Override
    public void run(String... args) throws InvalidProtocolBufferException {
        final String topic = "myTopic";

        final Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-consumer");

        try (final Consumer<String, byte[]> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(topic));
            int num = 0;
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, byte[]> record : records) {
                    num++;
                    String key = record.key();
                    BenchmarkMessageOuterClass.BenchmarkMessage message = BenchmarkMessageOuterClass.BenchmarkMessage.parseFrom(record.value());
                    if (message != null) {
                        logger.info("message {} consumed key: {}, message: {}", num, key, message.getMessage());
                        BenchmarkMessageDocument benchmarkMessageDocument = BenchmarkMessageDocument.builder()
                                .messageId(message.getMessageId())
                                .message(message.getMessage())
                                .build();
                        benchmarkMessageReactiveRepository.save(benchmarkMessageDocument).subscribe();
                    }
                }
            }
        }

    }
}