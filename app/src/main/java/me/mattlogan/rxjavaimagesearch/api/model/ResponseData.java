package me.mattlogan.rxjavaimagesearch.api.model;

import java.util.List;

public class ResponseData {

    private List<ImageData> results;

    public ResponseData(List<ImageData> results) {
        this.results = results;
    }

    public List<ImageData> getResults() {
        return results;
    }
}
