package me.mattlogan.rxjavaimagesearch.api.model;

import java.io.Serializable;

public class ImageData implements Serializable {

    private String tbUrl;
    private String url;

    public ImageData(String tbUrl, String url) {
        this.tbUrl = tbUrl;
        this.url = url;
    }

    public String getTbUrl() {
        return tbUrl;
    }

    public String getUrl() {
        return url;
    }
}
