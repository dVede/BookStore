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
import com.example.bookstore.Model.Comment;
import com.example.bookstore.Model.CommentItem;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;
import com.example.bookstore.Interfaces.VolleyCallBack;

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

public class CommentsFragmentViewModel extends AndroidViewModel {
    private List<CommentItem> mCommentsItems;
    private MutableLiveData<List<CommentItem>> mCartFragmentItem;
    private RequestQueue queue = QueueSingleton.getInstance(getApplication()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getApplication());

    public CommentsFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    private void addComment(CommentItem commentItem) {
        mCommentsItems.add(commentItem);
        mCartFragmentItem.postValue(mCommentsItems);
    }

    public MutableLiveData<List<CommentItem>> getBookComments(int bid) {
        if (mCartFragmentItem == null) {
            mCartFragmentItem = new MutableLiveData<>();
            loadBookComments(bid);
        }
        return mCartFragmentItem;
    }

    private void loadBookComments(int bid) {
        final JsonArrayRequest request = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.GET_BOOK_COMMENTS,
                        bid), response -> {
            try {
                mCommentsItems = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jrespone = response.getJSONObject(i);
                    final int cid = jrespone.getInt("commentid");
                    final String commentText = jrespone.getString("commenttext");
                    final int uid = jrespone.getInt("usertid");
                    final String username = jrespone.getString("username");
                    final Comment comment = new Comment(cid, bid, uid, commentText);
                    final CommentItem commentItem = new CommentItem(comment, username);
                    addComment(commentItem);
                }
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

    public void addComment(int bid, String comment) {
        request(bid, comment, () -> loadBookComments(bid));
    }

    private void request(int bid, String comment, final VolleyCallBack volleyCallBack) {
        final StringRequest requestAddComment = new StringRequest(Request.Method.POST,
                Utils.ADD_COMMENT,
                response -> volleyCallBack.onSuccess(),
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("uid",String.format(Locale.ENGLISH,"%d",preferencesHelper.getUser().getId()));
                params.put("bid",String.format(Locale.ENGLISH,"%d",bid));
                params.put("commenttext", comment);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestAddComment);
    }
}

