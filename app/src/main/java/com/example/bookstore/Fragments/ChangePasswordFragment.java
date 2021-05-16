package com.example.bookstore.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import org.postgresql.util.Base64;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private EditText password1, password2, password3;
    private String passwordText1, passwordText2, passwordText3;
    private Boolean isPreviousPasswordValid = false;
    private RequestQueue queue = QueueSingleton.getInstance(getContext()).getRequestQueue();
    private SharedPrefManager preferencesHelper = SharedPrefManager.getInstance(getContext());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_change_password, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        password1 = v.findViewById(R.id.EditText_Pwd2);
        password2 = v.findViewById(R.id.EditText_Pwd1);
        password3 = v.findViewById(R.id.EditText_Pwd3);
        final Button changePasswordButton = v.findViewById(R.id.change_password);

        changePasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        passwordText1 = password1.getText().toString();
        passwordText2 = password2.getText().toString();
        passwordText3 = password3.getText().toString();
        if (passwordText1.length() < 6 || passwordText2.length() < 6 || passwordText3.length() < 6
                || !passwordText2.equals(passwordText3) ){
            formatCheck();
        }
        else {
            final byte[] saltB = Utils.saltGenerate();
            final byte[] hashedB = Utils.hash(passwordText2.toCharArray(), saltB);
            final String salt = Base64.encodeBytes(saltB);
            final String hashed = Base64.encodeBytes(hashedB);

            checkPreviousPassword(passwordText1, () -> {
                if (isPreviousPasswordValid) {
                    if (!passwordText1.equals(passwordText2)) {
                        updatePassword(salt, hashed);
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                    else {
                        password1.setError(getResources().getString(R.string.different_values));
                        password2.setError(getResources().getString(R.string.different_values));
                    }
                }
                else password1.setError(getResources().getString(R.string.password_match));
            });
        }
    }

    private void checkPreviousPassword(String previousPassword, final VolleyCallBack volleyCallBack) {
        final JsonArrayRequest loginRequest = new JsonArrayRequest(
                String.format(Utils.GET_USER_PASSWORD, preferencesHelper.getUser().getLogin()),
                response -> {
                    try {
                        final char[] passwordInput = previousPassword.toCharArray();
                        final String salt = response.getJSONObject(0).getString("password_salt");
                        final String excepted = response.getJSONObject(0).getString("password_hash");
                        final byte[] salt2 = Base64.decode(salt);
                        final String actual = Base64.encodeBytes(Utils.hash(passwordInput, salt2));
                        if (excepted.equals(actual)) {
                            isPreviousPasswordValid = true;
                        }
                        volleyCallBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> System.out.println(error.getMessage())
        );
        queue.add(loginRequest);
    }

    private void updatePassword(String salt, String hashed) {
        final StringRequest requestChangePassword = new StringRequest(Request.Method.POST,
                Utils.CHANGE_PASSWORD,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("hashed",hashed);
                params.put("salt", salt);
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
        queue.add(requestChangePassword);
    }

    public void formatCheck(){
        if (password1.length() < 6) password1.setError("6 characters");
        if (password2.length() < 6) password2.setError("6 characters");
        if (password3.length() < 6) password3.setError("6 characters");
        if (passwordText1.isEmpty()) password1.setError("Empty");
        if (passwordText2.isEmpty()) password2.setError("Empty");
        if (passwordText3.isEmpty()) password3.setError("Empty");
        if (!passwordText2.equals(passwordText3))
            Toast.makeText(getContext(), "Password not match", Toast.LENGTH_SHORT).show();

    }
}