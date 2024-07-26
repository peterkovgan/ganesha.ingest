package com.ganesha.ingest;

import com.ganesha.ingest.page.Page;
import com.ganesha.ingest.schema.ParsingSchema;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class Reader {

    private final Map<String, ParsingSchema> schemaByTypeServices;

    @Autowired
    public Reader(List<ParsingSchema> schemaTypeServices) {
        schemaByTypeServices = schemaTypeServices.stream()
                .collect(Collectors.toMap(ParsingSchema::getSchemaId, Function.identity()));
    }

    public Optional<Page> read(String url) throws IOException {

        final AtomicReference<Optional<Page>> result = new AtomicReference<>(Optional.empty());

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url)
                    .build();
            httpclient.execute(httpGet, response -> {
                try {
                    log.info("Response from url {} response code {} reason {}", url,
                            response.getCode(), response.getReasonPhrase());
                    final HttpEntity entity1 = response.getEntity();
                    String body = EntityUtils.toString(entity1);

                    Optional<ParsingSchema> schema = allocateParsingSchema(url);

                    if(schema.isEmpty()) {
                        log.warn("There is no suitable schema for url {}", url);
                    }
                    result.set(Optional.of(schema.get().convert(body)));
                } finally {
                    response.close();
                }
                return result.get();
            });
        }
        return result.get();
    }

    private Optional<ParsingSchema> allocateParsingSchema(String url) {
        //regex special chars
        // https://www.threesl.com/blog/special-characters-regular-expressions-escape/
        //for https://www.bbc.com/news
        //key: ^https:\/\/www\.bbc\.com\/news$

        ParsingSchema schema = schemaByTypeServices.get("ListOfLinksSchema");
        Map<String, String> params = new HashMap<>();
        params.put("linkPattern", "<a\\sdata-testid=\"subNavigationLink\"\\shref=\"(.*?)\"\\sclass=\".*?\">(.*?)<\\/a>");
        params.put("parent", "https://www.bbc.com");
        schema.setParameters(params);
        return Optional.of(schema);
    }
}
