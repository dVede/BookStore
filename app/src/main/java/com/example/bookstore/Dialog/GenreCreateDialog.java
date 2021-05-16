package com.example.bookstore.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.Model.Genre;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.R;
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

public class GenreCreateDialog extends DialogFragment implements View.OnClickListener {

    private int parentId;
    private EditText genreEditText;
    private TextView parentGenreName;
    private List<Genre> genreList;
    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.genre_create_dialog, container,false);
        init(rootView);
        return rootView;
    }

    private void init(View v) {
        final Button cancelButton = v.findViewById(R.id.cancel_genre);
        final Button addButton = v.findViewById(R.id.add_genre);
        final Button chooseParentGenre = v.findViewById(R.id.choose_parent_genre);
        genreEditText = v.findViewById(R.id.genre_name_edit);
        parentGenreName = v.findViewById(R.id.parent_genre);
        chooseParentGenre.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_parent_genre:
                createPopupMenu(v);
                break;
            case R.id.add_genre:
                final String genre = genreEditText.getText().toString();
                if (genre.isEmpty()) {
                    genreEditText.setError(getResources().getString(R.string.empty));
                    break;
                }
                upsertGenre(genre, parentId);
                dismiss();
                break;
            case R.id.cancel_genre:
                dismiss();
                break;
        }
    }

    private void createPopupMenu(View v) {
        final PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.popup_genre_menu);
        getGenres(() -> {
            for (Genre genre : genreList) {
                popupMenu.getMenu().add(genre.getGenre());
                popupMenu.show();
                int pid = genre.getParentId();
                popupMenu.setOnMenuItemClickListener(item -> {
                    parentGenreName.setText(item.getTitle());
                    parentId = pid;
                    return true;
                });
            }
        });
    }

    private void getGenres(final VolleyCallBack volleyCallBack) {
        final JsonArrayRequest requestGenres = new JsonArrayRequest(
                Utils.GET_GENRES,
                response -> {
                    try {
                        genreList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            final JSONObject jrespone = response.getJSONObject(i);
                            final int gid = jrespone.getInt("genreid");
                            final String genre = jrespone.getString("name");
                            final int parentGenreId = jrespone.getInt("parentgenreid");
                            genreList.add(new Genre(gid, parentGenreId, genre));
                        }
                        volleyCallBack.onSuccess();
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
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    long now = System.currentTimeMillis();
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
        queue.add(requestGenres);
    }
    private void upsertGenre(String genre, int pgid) {
        final StringRequest requestGenre = new StringRequest(Request.Method.POST,
                Utils.ADD_GENRE,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("pgid",String.format(Locale.ENGLISH,"%d", pgid));
                params.put("genrenew",String.format(Locale.ENGLISH,"%s", genre));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestGenre);
    }
}
