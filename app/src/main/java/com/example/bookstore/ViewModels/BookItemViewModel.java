package com.example.bookstore.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.Fragments.BookFragment;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;
import com.example.bookstore.WishListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BookItemViewModel extends AndroidViewModel {
    private MutableLiveData<List<BookItem>> mBookItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());
    public BookItemViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<BookItem>> getBookItem(Class<?> c) {
        if (mBookItem == null) {
            mBookItem = new MutableLiveData<>();
            if (c == BookFragment.class) loadBookItems();
            else if (c == WishListFragment.class) parseJson();
        }
        return mBookItem;
    }

    public MutableLiveData<List<BookItem>> refresh(Class<?> c) {
        mBookItem = new MutableLiveData<>();
        if (c == BookFragment.class) loadBookItems();
        else if (c == WishListFragment.class) parseJson();
        return mBookItem;
    }

    private void loadBookItems() {
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
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
            }, Throwable::printStackTrace){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) new Cache.Entry();
                    final long cacheHitButRefreshed = 3 * 60 * 1000;
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    final long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    Objects.requireNonNull(cacheEntry).data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = Objects.requireNonNull(response.headers).get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString), cacheEntry);
                }  catch (UnsupportedEncodingException | JSONException e) {
                    return  Response.error(new ParseError(e));
                }
            }
        };
        queue.add(request);
    }

    private void parseJson() {
        final int userId = preferencesHelper.getUser().getId();
        final JsonArrayRequest request = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_ALL_WISHLISTED_BOOKS, userId), response -> {
            try {
                final List<BookItem> mBookList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);
                    final String imageurl = "https://static.tildacdn.com/tild3735-3738" +
                            "-4138-b132-613931396639/5a1d591289f629400560.png";
                    /*String imageurl = jrespone.getString("bimageurl");*/
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
                    final int id = jrespone.getInt("id");
                    final String isbn = jrespone.getString("isbn");
                    final double price = jrespone.getDouble("price");
                    mBookList.add(new BookItem(id, imageurl, title, isbn, price, authors));
                    mBookItem.setValue(mBookList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) new Cache.Entry();
                    final long cacheHitButRefreshed = 0;
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    final long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    Objects.requireNonNull(cacheEntry).data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = Objects.requireNonNull(response.headers).get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString), cacheEntry);
                }  catch (UnsupportedEncodingException | JSONException e) {
                    return  Response.error(new ParseError(e));
                }
            }
        };
        queue.add(request);
    }
}
