package com.ganesha.ingest;

import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Log4j2
@Component
public class Reader {

    public Optional<Page> read(String url) throws IOException {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url)
                    .build();
            httpclient.execute(httpGet, response -> {

                try {
                    System.out.println(response.getCode() + " " + response.getReasonPhrase());
                    final HttpEntity entity1 = response.getEntity();
                    String body = EntityUtils.toString(entity1);

                    Optional<ParsingSchema> schemaData = allocateParsingSchema(url);
                    if(schemaData.isEmpty()) {
                        log.warn("There is no suitable schema for url {}", url);
                        return Optional.empty();
                    }
                    //convert body to Page
                    return schemaData.get().convert(body);
                } finally {
                    response.close();
                }

            });
        }
        return Optional.empty();
    }

    private Optional<ParsingSchema> allocateParsingSchema(String url) {
        //TODO: load schema from DB, if does not exist in the cache

        return Optional.empty();
    }
}
