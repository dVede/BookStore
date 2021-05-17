package com.example.bookstore.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.Map;
import java.util.regex.Pattern;

public class AddAuthorDialog extends DialogFragment implements View.OnClickListener {

    private EditText lastNameEditText, firstNameEditText, middleNameEditText,imageUrlEditText;
    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.author_create_dialog, container,false);
        init(rootView);
        return rootView;
    }

    private void init(View v) {
        final Button addButton = v.findViewById(R.id.author_add);
        final Button cancelButton = v.findViewById(R.id.author_cancel);
        lastNameEditText = v.findViewById(R.id.editText_lastName);
        firstNameEditText = v.findViewById(R.id.editText_firstName);
        middleNameEditText = v.findViewById(R.id.editText_middleName);
        imageUrlEditText = v.findViewById(R.id.editText_imageUrl);

        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.author_add:
                final String imageUrl = imageUrlEditText.getText().toString();
                final String lastName = lastNameEditText.getText().toString();
                final String firstName = firstNameEditText.getText().toString();
                final String middleName = middleNameEditText.getText().toString();
                final boolean urlMatch = Pattern.matches(Utils.IMAGE_URL_REGEX, imageUrl);
                if ((lastName.isEmpty() && firstName.isEmpty() && middleName.isEmpty())
                        || imageUrl.isEmpty() || !urlMatch) {
                    validateInfo(lastName, firstName, middleName, imageUrl, urlMatch);
                    break;
                }
                addAuthor(lastName, firstName, middleName, imageUrl);
                dismiss();
                break;
            case R.id.author_cancel:
                dismiss();;
                break;
        }
    }

    private void validateInfo(String lastName, String firstName,
                              String middleName, String imageUrl, boolean urlMatch) {
        if (lastName.isEmpty() && firstName.isEmpty() && middleName.isEmpty()) {
            lastNameEditText.setError("At least one field must be filled");
            firstNameEditText.setError("At least one field must be filled");
            middleNameEditText.setError("At least one field must be filled");
        }
        if (!urlMatch) imageUrlEditText.setError("Incorrect URL");
        if (imageUrl.isEmpty()) imageUrlEditText.setError("Empty");
    }

    private void addAuthor(String lastName, String firstName, String middleName, String imageUrl) {
        final StringRequest requestAddAuthor = new StringRequest(Request.Method.POST,
                Utils.ADD_AUTHOR,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("lastnamenew", lastName);
                params.put("firstnamenew", firstName);
                params.put("middlenamenew", middleName);
                params.put("imageuralnew", imageUrl);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestAddAuthor);
    }
}
