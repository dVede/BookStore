package com.example.bookstore.ViewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BookItemViewModel extends AndroidViewModel implements RequestQueue.RequestEventListener {
    private MutableLiveData<List<BookItem>> mBookItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());
    private RequestQueue testQueue;
    private long startRequest;
    public BookItemViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<BookItem>> getBookItem() {
        testQueue = Volley.newRequestQueue(getApplication());
        testQueue.addRequestEventListener(this);
        if (mBookItem == null) {
            mBookItem = new MutableLiveData<>();
            loadBookItems();
        }
        return mBookItem;
    }

    private void loadBookItems() {
        final CacheRequest request = new CacheRequest(Request.Method.GET,
                Utils.GET_ALL_BOOKS, null, response -> {
            try {
                final List<BookItem> mBookList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);
                    final int id = jrespone.getInt("id");
                    final String imageurl = "https://static.tildacdn.com/tild3735-3738" +
                            "-4138-b132-613931396639/5a1d591289f629400560.png";
                    /*final String imageurl = jrespone.getString("bimageurl");*/
                    final String[] aids = jrespone.getString("aid").split(",");
                    final String[] aimageurls = jrespone.getString("aimageurl").split(",");
                    final String[] lastnames = jrespone.getString("lastname").split(",");
                    final String[] firstnames = jrespone.getString("firstname").split(",");
                    final String[] middlenames = jrespone.getString("middlename").split(",");
                    final List<Author> authors = new ArrayList<>();
                    for (int i1 = 0; i1 < lastnames.length; i1++) {
                        authors.add(new Author(Integer.parseInt(aids[i1]), lastnames[i1],
                                firstnames[i1], middlenames[i1], aimageurls[i1]));
                    }
                    final String title = jrespone.getString("title");
                    final String isbn = jrespone.getString("isbn");
                    final double price = jrespone.getDouble("price");
                    mBookList.add(new BookItem(id, imageurl, title, isbn, price, authors));
                }
                mBookItem.setValue(mBookList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }, volleyError -> {
            volleyError.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(volleyError, getApplication());
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_SHORT).show();
        });
        testQueue.add(request);
    }

    @Override
    public void onRequestEvent(Request<?> request, int event) {
        long endCache;
        long endNetwork;
        if (event == RequestQueue.RequestEvent.REQUEST_QUEUED)
            startRequest = System.nanoTime();
        if (event == RequestQueue.RequestEvent.REQUEST_CACHE_LOOKUP_FINISHED) {
            endCache = System.nanoTime();
            Timber.tag("cacheTime").d(String.valueOf(endCache - startRequest));
        }
        if (event == RequestQueue.RequestEvent.REQUEST_NETWORK_DISPATCH_FINISHED) {
            endNetwork = System.nanoTime();
            Timber.tag("networkTime").d(String.valueOf(endNetwork - startRequest));
        }
    }
}
