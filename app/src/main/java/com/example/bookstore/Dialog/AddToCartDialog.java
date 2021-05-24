package com.example.bookstore.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.R;
import com.example.bookstore.Utils;
import com.example.bookstore.Interfaces.VolleyCallBack;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddToCartDialog extends DialogFragment implements View.OnClickListener {

    private EditText amountEditText;
    private int bookId, consumerId;
    private boolean isInCart;
    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();

    public AddToCartDialog(int bookId, int consumerId) {
        this.bookId = bookId;
        this.consumerId = consumerId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_to_cart_dialog, container,false);
        init(rootView);
        return rootView;
    }

    private void init(View v) {
        final Button cancelButton = v.findViewById(R.id.cancel_dialog_button);
        final Button addButton = v.findViewById(R.id.add_dialog_button);
        amountEditText = v.findViewById(R.id.amount_dialog);
        cancelButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_dialog_button:
                dismiss();
                break;
            case R.id.add_dialog_button:
                final String amount = amountEditText.getText().toString();
                if (amount.isEmpty()) {
                    amountEditText.setError(getResources().getString(R.string.empty));
                    break;
                }
                int amountValue = Integer.parseInt(amount);
                if (Integer.parseInt(amount) == 0) {
                    amountEditText.setError(getResources().getString(R.string.amount_0_check));
                    break;
                }
                else isInCart(() -> {
                    if (isInCart) updateCart(amountValue);
                    else addToCart(amountValue);
                });
                dismiss();
                break;
        }
    }

    private void updateCart(int amount) {
        final StringRequest requestUpdateCart = new StringRequest(Request.Method.POST,
                Utils.UPDATE_CART,
                response -> { },
                error -> {
            error.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(error, getContext());
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("uid", String.valueOf(consumerId));
                params.put("bid", String.valueOf(bookId));
                params.put("amountt", String.valueOf(amount));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestUpdateCart);
    }

    private void addToCart(int amount) {
        final Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.getDefault());
        final String formattedDate = df.format(c);
        System.out.println(formattedDate);

        final StringRequest requestAddToCart = new StringRequest(Request.Method.POST,
                Utils.ADD_TO_CART,
                response -> { },
                error -> {
            final String errorMsg = Utils.getErrorMessage(error, getContext());
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("uid", String.valueOf(consumerId));
                params.put("bid", String.valueOf(bookId));
                params.put("times", formattedDate);
                params.put("amountt", String.valueOf(amount));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestAddToCart);
    }

    public void isInCart(final VolleyCallBack callBack) {
        JsonArrayRequest requestBookInfo = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.IS_IN_CART, consumerId, bookId),
                response -> {
                    try {
                        isInCart = response.getJSONObject(0).getBoolean("isincart");
                        callBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> System.out.println(error.getMessage()));
        queue.add(requestBookInfo);
    }
}
