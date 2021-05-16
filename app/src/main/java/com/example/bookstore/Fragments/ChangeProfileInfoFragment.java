package com.example.bookstore.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.Interfaces.VolleyCallBack;
import com.example.bookstore.R;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;
import com.example.bookstore.Utils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ChangeProfileInfoFragment extends Fragment implements View.OnClickListener {

    private EditText emailEditText, numberEditText, addressEditText;
    private Boolean isEmailExist, isTelephoneExist;
    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getContext());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_email, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        emailEditText = v.findViewById(R.id.EditText_email);
        numberEditText = v.findViewById(R.id.EditText_phoneNumber);
        addressEditText = v.findViewById(R.id.EditText_address);
        final Button changeInfoButton = v.findViewById(R.id.change_profile_info);

        changeInfoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String emailText = emailEditText.getText().toString();
        final String numberText = numberEditText.getText().toString();
        final String addressText = addressEditText.getText().toString();
        final boolean emailMatch = Pattern.matches(Utils.EMAIL_REGEX, emailText);
        final boolean numberMatch = Pattern.matches(Utils.TELEPHONE_REGEX, numberText);
        if (emailText.isEmpty() || numberText.isEmpty() || addressText.isEmpty()
                || numberText.length() != 12 || !emailMatch || !numberMatch)
            formatCheck(emailText, numberText, addressText, emailMatch, numberMatch);
        else
            validateInfo(emailText, numberText, () -> {
                if ((!isEmailExist) && (!isTelephoneExist)) {
                    updateInfo(emailText, numberText, addressText);
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    if (isEmailExist) emailEditText.setError(getResources().getString(R.string.email_used));
                    if (isTelephoneExist) numberEditText.setError(getResources().getString(R.string.number_used));
                }
            });
    }

    private void updateInfo(String emailText, String numberText, String addressText) {
        final StringRequest requestUpdateProfile = new StringRequest(Request.Method.POST,
                Utils.UPDATE_PROFILE_INFO,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("emailnew",emailText);
                params.put("telephonenew",numberText);
                params.put("addressnew",addressText);
                params.put("uid",String.format(Locale.ENGLISH,"%d",preferencesHelper.getUser().getId()));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestUpdateProfile);
    }

    private void validateInfo(String email, String telephone, final VolleyCallBack volleyCallBack) {
        JsonArrayRequest requestBookPublisher = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.EMAIL_TELEPHONE_CHECK, email, telephone),
                response -> {
                    try {
                        isEmailExist = response.getJSONObject(0).getBoolean("emailexist");
                        isTelephoneExist = response.getJSONObject(0).getBoolean("telephoneexist");
                        System.out.println(isEmailExist);
                        volleyCallBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> System.out.println(error.getMessage()));
        queue.add(requestBookPublisher);
    }

    private void formatCheck(String emailText, String numberText, String addressText,
                             boolean emailMatch, boolean numberMatch) {
        if (!emailMatch) emailEditText.setError(getResources().getString(R.string.email_regex_check));
        if (!numberMatch) numberEditText.setError(getResources().getString(R.string.phone_regex_check));
        if (numberEditText.length() != 12) numberEditText.setError(getResources().getString(R.string.characters12));
        if (numberText.isEmpty()) numberEditText.setError(getResources().getString(R.string.empty));
        if (addressText.isEmpty()) addressEditText.setError(getResources().getString(R.string.empty));
        if (emailText.isEmpty()) emailEditText.setError(getResources().getString(R.string.empty));
    }
}