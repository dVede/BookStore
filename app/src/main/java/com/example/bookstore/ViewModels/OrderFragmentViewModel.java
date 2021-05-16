package com.example.bookstore.ViewModels;

import android.app.Application;

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
import com.example.bookstore.Model.OrderItem;
import com.example.bookstore.Model.OrderStatus;
import com.example.bookstore.SingleLiveEvent;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderFragmentViewModel extends AndroidViewModel {
    private SingleLiveEvent<List<OrderItem>> mOrderItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());

    public OrderFragmentViewModel(@NonNull Application application) {
        super(application);
    }
    public SingleLiveEvent<List<OrderItem>> getOrderItem() {
        mOrderItem = new SingleLiveEvent<>();
        loadOrderItems();
        return mOrderItem;
    }

    private void loadOrderItems(){
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                String.format(Locale.ENGLISH, Utils.GET_ORDERS, preferencesHelper.getUser().getId()), null,
                response -> {
            try {
                final List<OrderItem> mOrderList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    final JSONObject jrespone = response.getJSONObject(i);

                    final String[] bids = jrespone.getString("bids").split(",");
                    final String status = jrespone.getString("status");
                    final String timestamp = jrespone.getString("times");
                    double totalSum = jrespone.getDouble("totalsum");
                    mOrderList.add(new OrderItem(bids, timestamp, OrderStatus.valueOf(status), totalSum));
                }
                mOrderItem.postValue(mOrderList);
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
                    long now = System.currentTimeMillis();
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
