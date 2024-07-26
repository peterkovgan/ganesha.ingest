package com.ganesha.ingest.page.kind;

import com.ganesha.ingest.page.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class ListOfLinksPage extends Page {
    @Getter
    private String parent;
    @Getter
    private List<String> links;

    @Override
    public String toContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("Links:\n");
        links.stream().forEach(e->{
            sb.append(parent);
            sb.append(e);
            sb.append("\n");
        });
        return sb.toString();
    }
}
