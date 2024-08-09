package com.ganesha.ingest.page.kind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ganesha.ingest.page.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "paragraphs"
})
public class ArticlePage extends Page{

    @Getter
    @Setter
    @JsonProperty("title")
    private String title;

    @Getter
    @Setter
    @JsonProperty("paragraphs")
    private List<String> paragraphs;

    public ArticlePage(int level, String parentUrl, String title, List<String> paragraphs) {
        super(level, parentUrl);
        this.title = title;
        this.paragraphs = paragraphs;
    }

    @Override
    public String toContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Title:\n");
        sb.append(title);
        sb.append("\n");
        sb.append("Paragraphs:\n");
        paragraphs.stream().forEach(e->{
            sb.append(e);
            sb.append("\n");
        });
        return sb.toString();
    }

    @Override
    public boolean isArticle() {
        return true;
    }
}
