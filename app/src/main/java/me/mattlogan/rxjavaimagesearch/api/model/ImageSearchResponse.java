package me.mattlogan.rxjavaimagesearch.api.model;

public class ImageSearchResponse {

    private ResponseData responseData;

    public ImageSearchResponse(ResponseData responseData) {
        this.responseData = responseData;
    }

    public ResponseData getResponseData() {
        return responseData;
    }
}
