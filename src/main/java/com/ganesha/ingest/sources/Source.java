package com.ganesha.ingest.sources;

import lombok.Data;

import java.util.Map;

@Data
public class Source {
    private Map<Integer, Map<String,String>> levels;
    public Map<String,String> getLevel(int level){
        return levels.get(level);
    }
}
