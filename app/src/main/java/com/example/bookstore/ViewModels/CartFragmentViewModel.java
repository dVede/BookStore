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
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.CartFragmentItem;
import com.example.bookstore.Model.CartItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CartFragmentViewModel extends AndroidViewModel {
    private MutableLiveData<CartFragmentItem> mCartFragmentItem;
    private List<CartItem> mCartItems;
    private CartFragmentItem cartFragmentItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());

    public CartFragmentViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<CartFragmentItem> getBookItem() {
        if (mCartFragmentItem == null) {
            mCartFragmentItem = new MutableLiveData<>();
            loadCartFragmentItems();
        }
        return mCartFragmentItem;
    }

    private void loadCartFragmentItems() {
        final JsonArrayRequest request = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_CART_BOOKS,
                        preferencesHelper.getUser().getId()), response -> {
            try {
                mCartItems = new ArrayList<>();
                double totalPrice = 0 ;
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);

                    final String[] aids = jrespone.getString("authorid").split(",");
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
                    final String bImageurl = jrespone.getString("bimageurl");
                    final BookItem bookItem = new BookItem(id, bImageurl, title, isbn, price, authors);
                    final int amount = jrespone.getInt("amount");
                    totalPrice += price * amount;
                    mCartItems.add(new CartItem(bookItem, amount));
                }
                cartFragmentItem = new CartFragmentItem(mCartItems, totalPrice);
                mCartFragmentItem.postValue(cartFragmentItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) new Cache.Entry();
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    final long now = System.currentTimeMillis();
                    Objects.requireNonNull(cacheEntry).data = response.data;
                    cacheEntry.softTtl = now;
                    cacheEntry.ttl = now + cacheExpired;
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

    public void deleteBookItem(int position) {
        List<CartItem> cartItems = cartFragmentItem.getCartItem();
        CartItem cartItem = cartItems.get(position);
        cartItems.remove(position);
        double booksPrice = cartItem.getAmount() * cartItem.getBookItem().getPrice();
        mCartItems = cartItems;
        mCartFragmentItem.postValue(new CartFragmentItem(cartItems, cartFragmentItem.getTotalPrice() - booksPrice));
        deleteBook(cartItem.getBookItem().getId());
    }

    private void deleteBook(int bid) {
        final StringRequest requestDeleteBook = new StringRequest(Request.Method.POST,
                Utils.DELETE_CART_BOOK,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("uid",String.format(Locale.ENGLISH,"%d",preferencesHelper.getUser().getId()));
                params.put("bid",String.format(Locale.ENGLISH,"%d",bid));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestDeleteBook);
    }
}
