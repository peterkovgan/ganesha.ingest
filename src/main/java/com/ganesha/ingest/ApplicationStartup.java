package com.ganesha.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesha.ingest.executor.JobStarter;
import com.ganesha.ingest.sources.ListSources;
import com.ganesha.ingest.sources.Source;
import com.ganesha.ingest.sources.Sources;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Log4j2
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Sources sources;
    @Autowired
    private JobStarter jobStarter;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        String listSourcesPath =  new ClassPathResource("src/main/resources/sources/sources.json").getPath();
        ObjectMapper mapper = new ObjectMapper();
        ListSources listSources = null;
        try {
            listSources = mapper.readValue(
                    new File(listSourcesPath), ListSources.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String url: listSources.getSources()){
            String convenientUrlName = url.replace(".","_")
                    .replace(":","_")
                    .replace("/","_");

            String clsPath =  new ClassPathResource("src/main/resources/sources/" + convenientUrlName + ".json").getPath();

            try {
                Source source = mapper.readValue(
                        new File(clsPath), Source.class);
                sources.addSource(url, source);
            } catch (IOException e) {
                e.printStackTrace();
            }

            jobStarter.startAsync(url, url,0);
        }
    }
}
