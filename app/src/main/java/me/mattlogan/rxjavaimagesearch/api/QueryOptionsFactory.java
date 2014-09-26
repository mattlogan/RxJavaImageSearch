package me.mattlogan.rxjavaimagesearch.api;

import java.util.HashMap;
import java.util.Map;

public class QueryOptionsFactory {

    public static Map<String, String> getQueryOptions(String query, int startIndex) {
        Map<String, String> options = new HashMap<String, String>();
        options.put("q", query);
        return options;
    }
}
