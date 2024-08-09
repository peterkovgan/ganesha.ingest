package com.ganesha.ingest.page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Page {

    @Getter
    protected int level;

    @Getter
    protected String parentUrl;


    public abstract String toContent();

    public abstract boolean isArticle();
}
