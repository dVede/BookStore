package com.example.bookstore.ViewModels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.Model.Author;
import com.example.bookstore.Model.BookItem;
import com.example.bookstore.Model.CartFragmentItem;
import com.example.bookstore.Model.CartItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        mCartFragmentItem = new MutableLiveData<>();
        loadCartFragmentItems();
        return mCartFragmentItem;
    }

    private void loadCartFragmentItems() {
        final CacheRequest request = new CacheRequest(
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
            }, volleyError -> {
                    volleyError.printStackTrace();
                    final String errorMsg = Utils.getErrorMessage(volleyError, getApplication());
                    Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_SHORT).show();
        });
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
                error -> {
            error.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(error, getApplication());
            Toast.makeText(getApplication(), errorMsg, Toast.LENGTH_SHORT).show();
        }){
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
