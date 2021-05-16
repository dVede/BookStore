package com.example.bookstore.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.Model.User;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Objects;


public class ProfileViewModel extends AndroidViewModel {
    private MutableLiveData<User> user;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<User> getUser() {
        user = new MutableLiveData<>();
        loadUser();
        return user;
    }

    private void loadUser() {
        final JsonArrayRequest requestUserInfo = new JsonArrayRequest(
                String.format(Utils.GET_USER_INFO, preferencesHelper.getUser().getLogin()),
                response -> {
                    try {
                        final int id = response.getJSONObject(0).getInt("id");
                        final String login = response.getJSONObject(0).getString("login");
                        final String email = response.getJSONObject(0).getString("email");
                        final String address = response.getJSONObject(0).getString("address");
                        final String telephone = response.getJSONObject(0).getString("telephone");
                        final User userInfo = new User(id, login, email, address, telephone, null);
                        user.postValue(userInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> System.out.println(error.getMessage())){
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
        queue.add(requestUserInfo);
    }
}