package com.example.bookstore.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.R;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RatingDialog extends DialogFragment implements View.OnClickListener {

    private int bookId, consumerId;

    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();
    private RatingBar ratingBar;

    public RatingDialog(int bookId, int consumerId) {
        this.bookId = bookId;
        this.consumerId = consumerId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rating_dialog, container,false);
        init(rootView);
        return rootView;
    }

    private void init(View v) {
        final Button cancelButton = v.findViewById(R.id.rate_cancel_button);
        final Button addButton = v.findViewById(R.id.rate_button);
        ratingBar = v.findViewById(R.id.ratingBar);
        cancelButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rate_cancel_button:
                dismiss();
            case R.id.rate_button:
                final float rating = ratingBar.getRating();
                upsertRating(rating, consumerId, bookId);
                dismiss();
        }
    }

    private void upsertRating(float rating, int consumerId, int bookId) {
        final int ratingInt = Math.round(rating) * 2;
        final StringRequest requestUpsertRating = new StringRequest(Request.Method.POST,
                Utils.UPSERT_RATING,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                System.out.println(consumerId);
                System.out.println(bookId);
                System.out.println(ratingInt);
                params.put("uid",String.format(Locale.ENGLISH,"%d", consumerId));
                params.put("bid",String.format(Locale.ENGLISH,"%d", bookId));
                params.put("rate",String.format(Locale.ENGLISH,"%d", ratingInt));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestUpsertRating);
    }
}
