package com.ganesha.ingest;

public interface ParsingSchema {
    Page convert(String body);
}
