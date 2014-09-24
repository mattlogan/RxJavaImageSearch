package me.mattlogan.rxjavaimagesearch;

import android.app.Application;

import me.mattlogan.rxjavaimagesearch.api.ImageSearchService;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class ImageSearchApplication extends Application {

    /**
     * Note: The Image Searcher supports a maximum of 8 result pages.
     * When combined with subsequent requests, a maximum total of 64 results are available.
     * It is not possible to request more than 64 results.
     */
    public static final int RESULTS_PER_PAGE = 8;
    public static final int MAXIMUM_RESULTS = 64;

    private static final String ENDPOINT = "https://ajax.googleapis.com";

    private static ImageSearchApplication instance;

    private ImageSearchService imageSearchService;

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        imageSearchService = buildImageSearchService();
    }

    ImageSearchService buildImageSearchService() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        request.addQueryParam("v", "1.0");
                        request.addQueryParam("rsz", Integer.toString(RESULTS_PER_PAGE));
                    }
                })
                .build()
                .create(ImageSearchService.class);
    }

    public static ImageSearchApplication get() {
        return instance;
    }

    public ImageSearchService getImageSearchService() {
        return imageSearchService;
    }
}
