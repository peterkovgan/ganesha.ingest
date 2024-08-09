package com.ganesha.ingest;

import com.ganesha.ingest.page.Page;
import com.ganesha.ingest.schema.ParsingSchema;
import com.ganesha.ingest.sources.Source;
import com.ganesha.ingest.sources.Sources;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class Reader {

    public static final String SCHEMA = "schema";
    @Autowired
    private Sources sources;

    private final Map<String, ParsingSchema> schemaByTypeServices;

    @Autowired
    public Reader(List<ParsingSchema> schemaTypeServices) {
        schemaByTypeServices = schemaTypeServices.stream()
                .collect(Collectors.toMap(ParsingSchema::getSchemaId, Function.identity()));
    }

    public Optional<Page> read(String baseUrl, String readUrl, int level) throws IOException {

        final AtomicReference<Optional<Page>> result = new AtomicReference<>(Optional.empty());

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(readUrl)
                    .build();
            httpclient.execute(httpGet, response -> {
                try {
                    log.info("Response from url {} response code {} reason {} level {}", readUrl,
                            response.getCode(), response.getReasonPhrase(), level);
                    final HttpEntity entity1 = response.getEntity();
                    String body = EntityUtils.toString(entity1);
                    Optional<ParsingSchema> schema = allocateParsingSchema(baseUrl, level);

                    if(schema.isEmpty()) {
                        log.warn("There is no suitable schema for url {}", baseUrl);
                    }
                    result.set(Optional.of(schema.get().convert(body, level, readUrl)));
                }catch(Exception e){
                    log.error("Reader exception url:{} level{}",readUrl, level, e);
                }finally {
                    response.close();
                }
                return result.get();
            });
        }
        return result.get();
    }

    private Optional<ParsingSchema> allocateParsingSchema(String url, int level) {
        Source source = sources.getSource(url);
        Map<String, String> sourceLevelProps = source.getLevel(level);
        String schemaType = sourceLevelProps.get(SCHEMA);
        ParsingSchema schema = schemaByTypeServices.get(schemaType);
        schema.setParameters(sourceLevelProps);
        return Optional.of(schema);
    }
}
