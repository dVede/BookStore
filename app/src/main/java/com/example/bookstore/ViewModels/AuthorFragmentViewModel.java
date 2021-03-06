package com.example.bookstore.ViewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.CacheRequestTtl;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.AuthorItem;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.SingleLiveEvent;
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

public class AuthorFragmentViewModel extends AndroidViewModel {
    private SingleLiveEvent<AuthorItem> mAuthorItem;
    private List<BookItem> mBookItems;
    private AuthorItem authorItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();

    public AuthorFragmentViewModel(@NonNull Application application) {
        super(application);
    }
    public SingleLiveEvent<AuthorItem> getBookItem(int authorId) {
        mAuthorItem = new SingleLiveEvent<>();
        loadAuthorItems(authorId);
        return mAuthorItem;
    }

    private void loadAuthorItems(int aid) {
        final CacheRequest request = new CacheRequest(Request.Method.GET,
                String.format(Locale.ENGLISH, Utils.GET_AUTHOR_BOOKS, aid), null, response -> {
            try {
                final int authorId = response.getJSONObject(0).getInt("authorid");
                final String lastname = response.getJSONObject(0).getString("lastname");
                final String firstname = response.getJSONObject(0).getString("firstname");
                final String middlename = response.getJSONObject(0).getString("middlename");
                final String imageurl = response.getJSONObject(0).getString("aimageurl");
                final Author author = new Author(authorId, lastname, firstname, middlename, imageurl);
                final List<Author> authors = new ArrayList<>();
                mBookItems = new ArrayList<>();
                authors.add(author);
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);

                    final String bImageurl = jrespone.getString("bimageurl");
                    final String title = jrespone.getString("title");
                    final int id = jrespone.getInt("id");
                    final String isbn = jrespone.getString("isbn");
                    final double price = jrespone.getDouble("price");
                    mBookItems.add(new BookItem(id, bImageurl, title, isbn, price, authors));
                }
                authorItem = new AuthorItem(author, mBookItems);
                mAuthorItem.postValue(authorItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            volleyError.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(volleyError, getApplication());
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }
}
