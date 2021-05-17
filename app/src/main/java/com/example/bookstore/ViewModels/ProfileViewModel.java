package com.example.bookstore.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.CacheRequest;
import com.example.bookstore.CacheRequestTtl;
import com.example.bookstore.Model.User;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;

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
        final CacheRequest requestUserInfo = new CacheRequest(
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
                error -> System.out.println(error.getMessage()));
        queue.add(requestUserInfo);
    }
}