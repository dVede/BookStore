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
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.Publisher;
import com.example.bookstore.Model.PublisherItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingleLiveEvent;
import com.example.bookstore.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PublisherFragmentViewModel extends AndroidViewModel {
    private SingleLiveEvent<PublisherItem> mPublisherItem;
    private List<BookItem> mBookItems;
    private PublisherItem publisherItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();

    public PublisherFragmentViewModel(@NonNull Application application) {
        super(application);
    }
    public SingleLiveEvent<PublisherItem> getBookItem(int publisherId) {
        mPublisherItem = new SingleLiveEvent<>();
        loadPublisherItems(publisherId);
        return mPublisherItem;
    }

    private void loadPublisherItems(int pid) {
        final CacheRequest request = new CacheRequest(Request.Method.GET,
                String.format(Locale.ENGLISH, Utils.GET_PUBLISHER_BOOK, pid), null, response -> {
            try {
                mBookItems = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);
                    final String imageurl = jrespone.getString("bimageurl");
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
                    mBookItems.add(new BookItem(id, imageurl, title, isbn, price, authors));
                }
                final int publisherId = response.getJSONObject(0).getInt("publisherid");
                final String name = response.getJSONObject(0).getString("name");
                final String address = response.getJSONObject(0).getString("address");
                final String email = response.getJSONObject(0).getString("email");

                final Publisher publisher = new Publisher(publisherId,name, address, email);
                publisherItem = new PublisherItem(publisher, mBookItems);
                mPublisherItem.postValue(publisherItem);
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
