package com.example.bookstore.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.Model.OrderItem;
import com.example.bookstore.Model.OrderStatus;
import com.example.bookstore.SingleLiveEvent;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        final CacheRequest request = new CacheRequest(Request.Method.GET,
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
        }, Throwable::printStackTrace);
        queue.add(request);
    }
}
