package com.ganesha.ingest.schema.kind;

import com.ganesha.ingest.page.kind.ListOfLinksPage;
import com.ganesha.ingest.schema.ParametrizedParsingSchema;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@NoArgsConstructor
public class ListOfLinksSchema extends ParametrizedParsingSchema<ListOfLinksPage>
{

    public static final String LINK_PATTERN = "linkPattern";
    public static final String PARENT = "parent";

    @Override
    public ListOfLinksPage convert(String body) {

        String linkPattern = params.get(LINK_PATTERN);
        String parent = params.get(PARENT);

        List<String> allMatches = new ArrayList<>();

        Pattern pattern = Pattern.compile(linkPattern);

        Matcher m = pattern.matcher(body);

        while (m.find()) {
            allMatches.add(m.group());
        }

        List<String> links = allMatches.stream().map(e->{
            Matcher linkMatcher = pattern.matcher(e);
            if(linkMatcher.matches()){
                String url = linkMatcher.group(1);
                return url;
            }
            return null;
        }).filter(e->e!=null).toList();

        return new ListOfLinksPage(parent, links);
    }

    @Override
    public String getSchemaId() {
        return this.getClass().getSimpleName();
    }
}
