package com.ganesha.ingest.executor;

import com.ganesha.ingest.Reader;
import com.ganesha.ingest.kafka.KafkaEndpoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class JobStarter {
    @Autowired
    private Reader reader;

    @Autowired
    private KafkaEndpoint kafkaEndpoint;

    private final ScheduledExecutorService executor;
    public JobStarter(){
        executor = Executors.newScheduledThreadPool(10);
    }

    public void startAsync(String baseUrl, String readUrl, int level) {
        Fetcher fetcher = new Fetcher(reader, baseUrl, readUrl, level, this, kafkaEndpoint);
        int randomTime = (new Random()).nextInt(3000, 10000);
        executor.schedule(fetcher, randomTime, TimeUnit.MILLISECONDS);
    }
}
