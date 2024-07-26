package com.ganesha.ingest;

import com.ganesha.ingest.page.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Reader reader;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        //it is spot instance that just started and configured for N sites/executors

        //load list of sites to process

        //create 1 executor per site

        //each executor must find an instance id of the previously completed flow
        //Variants:
        //1. if found previous instance id, if flow was not completed - start this flow from the last completed + 1 step
        //2. if there is no previous flow id - start new flow id, it is first run for the site, start step 0
        //3. if found previous instance id, if flow was completed, start new flow and start from the step 0

        //TODO: implement the logic above

        //TODO: implement site->executor map
        //TODO: start flow from step X, implement how new flow step starts after the last with delay

        try {
            Page page = reader.read("https://www.bbc.com/news").get();
            log.info("Page type {} content {}", page.getClass().getSimpleName(), page.toContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
