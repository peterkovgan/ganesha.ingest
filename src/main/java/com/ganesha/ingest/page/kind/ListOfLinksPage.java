package com.ganesha.ingest.page.kind;

import com.ganesha.ingest.page.Page;
import lombok.Getter;

import java.util.List;

public class ListOfLinksPage extends Page {
    @Getter
    private List<String> links;

    public ListOfLinksPage(int level, String parentUrl, List<String> links) {
        super(level, parentUrl);
        this.links = links;
    }

    @Override
    public String toContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Parent url:\n");
        sb.append(parentUrl);
        sb.append("\n");
        sb.append("Links:\n");
        links.stream().forEach(e->{
            sb.append(e);
            sb.append("\n");
        });
        return sb.toString();
    }

    @Override
    public boolean isArticle() {
        return false;
    }
}
