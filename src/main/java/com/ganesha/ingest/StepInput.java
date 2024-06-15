package com.ganesha.ingest;

import lombok.Data;

@Data
public class StepInput {
    private String flowId;
    private String url;
    private String stepId;
}
