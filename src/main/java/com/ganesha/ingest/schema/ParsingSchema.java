package com.ganesha.ingest.schema;

import com.ganesha.ingest.page.Page;

import java.util.Map;

public interface ParsingSchema<T extends Page> {
    T convert(String body, int level, String url);
    String getSchemaId();
    void setParameters(Map<String, String> params);
}
