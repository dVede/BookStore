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
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.R;
import com.example.bookstore.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PublisherAddDialog extends DialogFragment implements View.OnClickListener {

    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();
    private EditText nameText, addressText, emailText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.publisher_add_dialog, container,false);
        init(rootView);
        return rootView;
    }

    private void init(View v) {
        final Button cancelButton = v.findViewById(R.id.cancel_publisher_data);
        final Button addButton = v.findViewById(R.id.add_publisher_data);
        emailText = v.findViewById(R.id.pubisher_email_edit);
        addressText = v.findViewById(R.id.pubisher_address_edit);
        nameText = v.findViewById(R.id.pubisher_name_edit);
        cancelButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String email = emailText.getText().toString();
        final String address = addressText.getText().toString();
        final String name = nameText.getText().toString();
        final boolean emailMatch = Pattern.matches(Utils.EMAIL_REGEX, email);
        switch (v.getId()) {
            case R.id.cancel_publisher_data:
                dismiss();
                break;
            case R.id.add_publisher_data:
                if (email.isEmpty() || name.isEmpty() || address.isEmpty() || !emailMatch)
                    formatCheck(email, address, name, emailMatch);
                else {
                    addPublisher(email, address, name);
                    dismiss();
                }
                break;
        }
    }

    private void addPublisher(String email, String address, String name) {
        final StringRequest requestUpdateCart = new StringRequest(Request.Method.POST,
                Utils.ADD_PUBLISHER,
                response -> { },
                error -> {
            error.printStackTrace();
            final String errorMsg = Utils.getErrorMessage(error, getContext());
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("pemail", email);
                params.put("paddress", address);
                params.put("pname", name);
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

    private void formatCheck(String email, String address, String name, boolean emailMatch) {
        if (!emailMatch) emailText.setError(getResources().getString(R.string.email_regex_check));
        if (email.isEmpty()) emailText.setError(getResources().getString(R.string.empty));
        if (address.isEmpty()) addressText.setError(getResources().getString(R.string.empty));
        if (name.isEmpty()) nameText.setError(getResources().getString(R.string.empty));
    }
}
