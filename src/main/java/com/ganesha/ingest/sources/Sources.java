package com.ganesha.ingest.sources;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Sources {
    private Map<String, Source> sources = new HashMap<>();
    public void addSource(String id, Source value){
        sources.put(id, value);
    }
    public Source getSource(String id){
        return sources.get(id);
    }
}
