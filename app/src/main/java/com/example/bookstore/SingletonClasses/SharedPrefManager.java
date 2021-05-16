package com.example.bookstore.SingletonClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

import com.example.bookstore.Model.BookDetailed;
import com.example.bookstore.Model.User;
import com.example.bookstore.Utils;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static LruCache<Integer, BookDetailed> mCache;
    private final Context ctx;

    private SharedPrefManager(Context ctx) {
        this.ctx = ctx;
    }

    public static synchronized SharedPrefManager getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(ctx);
        }
        return mInstance;
    }

    public void saveUser(User user) {
        final SharedPreferences sharedPreferences = ctx
                .getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", user.getId());
        editor.putString("login", user.getLogin());
        editor.putString("token", user.getToken());
        editor.apply();
    }

    public User getUser() {
        final SharedPreferences sharedPreferences = ctx
                .getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt("id", 0),
                sharedPreferences.getString("login", null),
                sharedPreferences.getString("token", null)
        );
    }

    public boolean isLoggedIn() {
        final SharedPreferences sharedPreferences = ctx.getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", null) != null;
    }

    public void saveCache(){
        final SharedPreferences sharedPreferences = ctx
                .getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BOOK_DETAILED_CACHED", "caching");
        editor.apply();
    }

    public static BookDetailed getBookDetailedMemoryCache(int key) {
        return mCache.get(key);
    }

    public static BookDetailed setBookDetailedMemoryCache(int key, BookDetailed bookDetailed) {
        return mCache.put(key, bookDetailed);
    }


    public void clear() {
        final SharedPreferences sharedPreferences = ctx.getSharedPreferences(Utils.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
