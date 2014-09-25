package me.mattlogan.rxjavaimagesearch;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.mattlogan.rxjavaimagesearch.api.ImageSearchService;
import me.mattlogan.rxjavaimagesearch.api.QueryOptionsFactory;
import me.mattlogan.rxjavaimagesearch.api.model.ImageData;
import me.mattlogan.rxjavaimagesearch.api.model.ImageSearchResponse;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private ImageSearchService imageSearchService;

    @InjectView(R.id.image_search_edit_text) EditText editText;
    @InjectView(R.id.image_search_grid_view) GridView gridView;

    private ImageSearchGridAdapter adapter;

    private int lastImageFetchStartIndex;

    private static final String IMAGE_DATA_LIST_KEY = "image_data_list";
    private List<ImageData> imageDataList;

    @SuppressWarnings("unchecked")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        imageSearchService = ImageSearchApplication.get().getImageSearchService();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView textView, int actionId,
                                                    KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imageDataList.clear();
                    hideKeyboard();
                    requestImageFetch(editText.getText().toString(), 0);
                    return true;
                }
                return false;
            }
        });

        adapter = new ImageSearchGridAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && totalItemCount != lastImageFetchStartIndex) {
                    requestImageFetch(editText.getText().toString(), totalItemCount);
                }
            }
        });

        if (savedInstanceState != null) {
            imageDataList =
                    (List<ImageData>) savedInstanceState.getSerializable(IMAGE_DATA_LIST_KEY);
        } else {
            imageDataList = new ArrayList<ImageData>();
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(IMAGE_DATA_LIST_KEY, (Serializable) imageDataList);
    }

    private void requestImageFetch(String query, int startIndex) {
        if (startIndex + ImageSearchApplication.RESULTS_PER_PAGE <=
                ImageSearchApplication.MAXIMUM_RESULTS) {

            imageSearchService
                    .getImages(QueryOptionsFactory.getQueryOptions(query, startIndex))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ImageSearchResponse>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, "Failed to load images",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(final ImageSearchResponse imageSearchResponse) {
                            imageDataList.addAll(
                                    imageSearchResponse.getResponseData().getResults());
                            adapter.notifyDataSetChanged();
                        }
                    });

            lastImageFetchStartIndex = startIndex;
        }
    }

    private class ImageSearchGridAdapter extends BaseAdapter {

        private Context context;

        private ImageSearchGridAdapter(Context context) {
            this.context = context;
        }

        @Override public int getCount() {
            return imageDataList == null ? 0 : imageDataList.size();
        }

        @Override public Object getItem(int position) {
            return imageDataList == null ? null : imageDataList.get(position);
        }

        @Override public long getItemId(int position) {
            return 0;
        }

        @Override public View getView(int position, View view, ViewGroup parent) {
            ImageView imageView = (ImageView) view;
            if (imageView == null) {
                imageView = new ImageView(context);
                int height = (int) (context.getResources().getDisplayMetrics().widthPixels / 3f);
                imageView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            Picasso.with(context)
                    .load(imageDataList.get(position).getTbUrl())
                    .into(imageView);

            return imageView;
        }
    }
}
