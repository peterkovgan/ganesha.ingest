package com.ganesha.ingest.schema;

import com.ganesha.ingest.page.Page;

import java.util.Map;

public abstract class ParametrizedParsingSchema<T extends Page> implements ParsingSchema<T>{

    protected Map<String, String> params;

    @Override
    public void setParameters(Map<String, String> params) {
        this.params = params;
    }
}
