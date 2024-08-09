package com.ganesha.ingest.kafka;

import com.ganesha.ingest.page.kind.ArticlePage;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
public class KafkaEndpoint {

    public static final String INGEST_EVENTS = "ingest-events";
    private Producer<String, ArticlePage> producer;

    public KafkaEndpoint(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("linger.ms", 1);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", JsonSerializer.class);
        producer = new KafkaProducer<>(props);
    }

    public void sendArticlePage(ArticlePage page) {
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, ArticlePage> record =
                new ProducerRecord<>(INGEST_EVENTS, key, page);
        try {
            RecordMetadata recordMetadata = producer.send(record).get();
            String message = String.format("sent message to topic:%s partition:%s  offset:%s",
                    recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
            log.info("Sent message to Kafka {}", message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
