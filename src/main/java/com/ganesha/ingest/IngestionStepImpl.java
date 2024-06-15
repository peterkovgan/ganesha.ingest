package com.ganesha.ingest;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;


@Log4j2
@Component
public class IngestionStepImpl implements IngestionStep {

    @Autowired
    private Reader reader;

    @Autowired
    private IngestDao ingestDao;

    @Override
    public void execute(String flowId) {

        //get last uncompleted step for the flowId
        Optional<StepInput> stepData = getLastUncompletedStep();
        if(stepData.isEmpty()){
            log.info("No new step found for {} , finish", flowId);
            return;
        }

        //get Url of the step
        String url = stepData.get().getUrl();

        log.info("Going to process url {}, flow {}, step {}",
                url, flowId, stepData.get().getStepId());

        Optional<Page> pageData = Optional.empty();

        try {
            pageData = reader.read(url);
        } catch (IOException e) {
            log.error("Failed to read url {} in the flow {} step {}",
                    url, flowId, stepData.get().getStepId());
        }

        if(pageData.isEmpty()) {
            log.info("Page is not available for url {} in the flow {} step {}",
                    url, flowId, stepData.get().getStepId());
        }

        persistPage(pageData.get(), flowId, stepData.get().getStepId(), url);
        log.info("Completed url {} flow {} step {}",
                url, flowId, stepData.get().getStepId());

    }

    private void persistPage(Page page, String flowId, String stepId, String url) {
        ingestDao.persist(page, flowId, stepId, url);
    }

    private Optional<StepInput> getLastUncompletedStep() {
        return Optional.empty();
    }
}
