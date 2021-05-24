package com.example.bookstore.ViewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.CacheRequestTtl;
import com.example.bookstore.Fragments.BookFragment;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.CartFragmentItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WishListViewModel extends AndroidViewModel {
    private MutableLiveData<List<BookItem>> mBookItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());

    public WishListViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<List<BookItem>> getWishlistBooks() {
        mBookItem = new MutableLiveData<>();
        parseJson();
        return mBookItem;
    }

    private void parseJson() {
        final int userId = preferencesHelper.getUser().getId();
        final CacheRequestTtl request = new CacheRequestTtl(
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
            }, volleyError -> {
                    volleyError.printStackTrace();
                    final String errorMsg = Utils.getErrorMessage(volleyError, getApplication());
                    Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }
}
