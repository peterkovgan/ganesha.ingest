package com.ganesha.ingest.executor;

import com.ganesha.ingest.Reader;
import com.ganesha.ingest.kafka.KafkaEndpoint;
import com.ganesha.ingest.page.Page;
import com.ganesha.ingest.page.kind.ArticlePage;
import com.ganesha.ingest.page.kind.ListOfLinksPage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.concurrent.Callable;

@AllArgsConstructor
@Log4j2
public class Fetcher implements Callable<Page> {


    private final Reader reader;
    private final String baseUrl;
    private final String readUrl;
    private final int level;
    private final JobStarter jobStarter;
    private final KafkaEndpoint kafkaEndpoint;

    @Override
    public Page call() throws Exception {
        try {
            Page page = reader.read(baseUrl, readUrl, level).get();
            log.info("Succeed to fetch page type {} content {}", page.getClass().getSimpleName(), page.toContent());
            if(!page.isArticle()){
                ListOfLinksPage pageOfLinks = (ListOfLinksPage)page;
                log.info("Got {} links in the page", pageOfLinks.getLinks().size());
                pageOfLinks.getLinks().forEach(link->{
                    int newLevel = page.getLevel() + 1;
                    log.info("Scheduling new job {} {} {}", baseUrl, link, newLevel);
                    jobStarter.startAsync(baseUrl, link, newLevel);
                });
            }else{
                //end result - article page
                //send it to the queue
                kafkaEndpoint.sendArticlePage((ArticlePage) page);
            }
            return page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
