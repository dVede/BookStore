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
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.Genre;
import com.example.bookstore.Model.GenreItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GenreFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<GenreItem> mGenreItem;
    private List<BookItem> mBookItems;
    private GenreItem genreItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();

    public GenreFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<GenreItem> getBookItem(int gid) {
        if (mGenreItem == null) {
            mGenreItem = new MutableLiveData<>();
            loadGenreItems(gid);
        }
        return mGenreItem;
    }

    private void loadGenreItems(int gid) {
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                String.format(Locale.ENGLISH, Utils.GET_GENRE_BOOKS, gid), null, response -> {
            try {
                mBookItems = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);
                    final String imageurl = jrespone.getString("bimageurl");
                    final String[] aids = jrespone.getString("authorid").split(",");
                    final String[] aimageurls = jrespone.getString("aimageurl").split(",");
                    final  String[] lastnames = jrespone.getString("lastname").split(",");
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
                    mBookItems.add(new BookItem(id, imageurl, title, isbn, price, authors));
                }
                final int genreId = response.getJSONObject(0).getInt("genreid");
                final String genre = response.getJSONObject(0).getString("genre");
                final int genreParentId = response.getJSONObject(0).getInt("genreparentid");

                final Genre mGenre = new Genre(genreId, genreParentId, genre);
                genreItem = new GenreItem(mGenre, mBookItems);
                mGenreItem.postValue(genreItem);
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
}