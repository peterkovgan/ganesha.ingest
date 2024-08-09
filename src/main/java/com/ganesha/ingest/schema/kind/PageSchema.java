package com.ganesha.ingest.schema.kind;

import com.ganesha.ingest.page.kind.ArticlePage;
import com.ganesha.ingest.schema.ParametrizedParsingSchema;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@NoArgsConstructor
@Log4j2
public class PageSchema extends ParametrizedParsingSchema<ArticlePage>
{
    public static final String TITLE_PATTERN = "titlePattern";
    public static final String PARAGRAPH_PATTERN = "paragraphPattern";
    public static final String TITLE_GROUP = "titleGroup";
    public static final String PARAGRAPH_GROUP = "paragraphGroup";

    @Override
    public ArticlePage convert(String body, int level, String calledUrl) {

        String titlePattern = params.get(TITLE_PATTERN);
        int titleGroup = Integer.valueOf(params.get(TITLE_GROUP));
        List<String> titles = extractPart(body, titlePattern, titleGroup);

        log.info("Title:{}", titles.get(0));

        String paragraphPattern = params.get(PARAGRAPH_PATTERN);
        int paragraphGroup = Integer.valueOf(params.get(PARAGRAPH_GROUP));
        List<String> paragraphs = extractPart(body, paragraphPattern, paragraphGroup);

        log.info("Paragraph:{}", paragraphs.get(0));


        return new ArticlePage(level, calledUrl, titles.get(0), paragraphs);
    }

    private List<String> extractPart(String body, String elementPattern, int elementGroup) {
        List<String> allMatches = new ArrayList<>();
        Pattern pattern = Pattern.compile(elementPattern);
        Matcher m = pattern.matcher(body);
        while (m.find()) {
            allMatches.add(m.group());
        }
        List<String> articleTitles = allMatches.stream().map(e->{
            Matcher articleTitleMatcher = pattern.matcher(e);
            if(articleTitleMatcher.matches()){
                String title = articleTitleMatcher.group(elementGroup);
                return title;
            }
            return null;
        }).filter(e->e!=null).toList();
        return articleTitles;
    }

    @Override
    public String getSchemaId() {
        return this.getClass().getSimpleName();
    }
}
