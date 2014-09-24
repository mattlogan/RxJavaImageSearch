package me.mattlogan.rxjavaimagesearch.api;

import java.util.Map;

import me.mattlogan.rxjavaimagesearch.api.model.ImageSearchResponse;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

public interface ImageSearchService {
    @GET("/ajax/services/search/images")
    public Observable<ImageSearchResponse> getImages(@QueryMap Map<String, String> options);
}
