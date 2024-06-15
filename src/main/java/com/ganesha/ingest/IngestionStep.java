package com.ganesha.ingest;

public interface IngestionStep {

    void execute(String flowId);
}
