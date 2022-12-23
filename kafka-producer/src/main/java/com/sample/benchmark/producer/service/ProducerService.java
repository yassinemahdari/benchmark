package com.sample.benchmark.producer.service;

import com.sample.benchmark.BenchmarkMessageOuterClass;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Properties;
import java.util.UUID;

@Service
public class ProducerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    private FluxSink<SenderRecord<String, byte[], String>> fluxSink;

    public ProducerService() {
        logger.info("Initializing Reactor Kafka producer");
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        SenderOptions<String, byte[]> senderOptions = SenderOptions.create(properties);

        KafkaSender<String, byte[]> sender = KafkaSender.create(senderOptions);

        Flux<SenderRecord<String, byte[], String>> recordFlux = Flux.create(s -> fluxSink = s);

        sender.send(recordFlux)
                .doOnError(e -> logger.error("Send failed", e))
                .subscribe(record -> {
                    RecordMetadata recordMetadata = record.recordMetadata();
                    logger.debug("Message sent successfully, topic partition: {}, offset: {}",
                            recordMetadata.partition(),
                            recordMetadata.offset()
                    );
                });
    }

    @Override
    public void run(String... args) {
        logger.info("Start publishing messages");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int count = 0;
        while (count < 50000) {
            String messageId = UUID.randomUUID().toString();
            BenchmarkMessageOuterClass.BenchmarkMessage message = BenchmarkMessageOuterClass.BenchmarkMessage.newBuilder()
                    .setMessageId(messageId)
                    .setMessage(String.format("Hello %s !", messageId))
                    .build();
            ProducerRecord<String, byte[]> record = new ProducerRecord<>("myTopic", UUID.randomUUID().toString(), message.toByteArray());
            fluxSink.next(SenderRecord.create(record, record.key()));
            count++;
        }
        stopWatch.stop();
        logger.info("All messages are published in {} seconds", stopWatch.getTotalTimeSeconds());
    }
}
