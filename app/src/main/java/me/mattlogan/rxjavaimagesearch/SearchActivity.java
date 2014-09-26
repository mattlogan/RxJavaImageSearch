package me.mattlogan.rxjavaimagesearch;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mattlogan.rxjavaimagesearch.api.ImageSearchService;
import me.mattlogan.rxjavaimagesearch.api.QueryOptionsFactory;
import me.mattlogan.rxjavaimagesearch.api.model.ImageData;
import me.mattlogan.rxjavaimagesearch.api.model.ImageSearchResponse;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class SearchActivity extends BaseActivity {

    @InjectView(R.id.image_search_edit_text) EditText editText;
    @InjectView(R.id.image_result_image_view) ImageView imageView;

    ImageSearchService imageSearchService;

    Subscription subscription;
    PublishSubject<Observable<ImageSearchResponse>> searchTextEmitterSubject;

    @SuppressWarnings("unchecked")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        imageSearchService = ImageSearchApplication.get().getImageSearchService();

        searchTextEmitterSubject = PublishSubject.create();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i2, int i3) {
                Log.d("testing", "onTextChanged");
                searchTextEmitterSubject.onNext(imageSearchService.getImages(
                                QueryOptionsFactory.getQueryOptions(charSequence.toString(), 0)));
            }

            @Override public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        subscription = AndroidObservable.bindActivity(this,
                Observable.switchOnNext(searchTextEmitterSubject))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override public void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }

    Observer<ImageSearchResponse> observer = new Observer<ImageSearchResponse>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable e) {
            Log.d("testing", "e: " + e);
            Toast.makeText(SearchActivity.this, "Failed to load images", Toast.LENGTH_LONG).show();
        }

        @Override public void onNext(ImageSearchResponse imageSearchResponse) {
            Log.d("testing", "onNext");
            List<ImageData> results = imageSearchResponse.getResponseData().getResults();
            if (results != null && results.size() > 0) {
                String url = results.get(0).getUrl();
                Picasso.with(SearchActivity.this)
                        .load(url)
                        .into(imageView);
            }
        }
    };
}
