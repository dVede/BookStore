package com.example.bookstore;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookstore.Interfaces.VolleyCallBack;
import com.example.bookstore.SingletonClasses.QueueSingleton;

import org.json.JSONException;
import org.postgresql.util.Base64;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private RequestQueue queue;
    private EditText emailEditText, loginEditText, phoneEditText, addressEditText, passwordEditText;
    private boolean isEmailExist, isPhoneExist, isLoginExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        queue = QueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        loginEditText = findViewById(R.id.editTextTextPersonName);
        phoneEditText = findViewById(R.id.editTextNumber);
        addressEditText = findViewById(R.id.editTextTextPostalAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        final Button registerButton = findViewById(R.id.register_button);
        final TextView backToLoginButton = findViewById(R.id.already_have_account);
        backToLoginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                tryToCreateAccount();
                break;
            case R.id.already_have_account:
                finish();
                break;
        }
    }

    private void tryToCreateAccount() {
        final String email = emailEditText.getText().toString();
        final String login = loginEditText.getText().toString();
        final String phone = phoneEditText.getText().toString();
        final String address = addressEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        final boolean emailMatch = Pattern.matches(Utils.EMAIL_REGEX, email);
        final boolean phoneMatch = Pattern.matches(Utils.TELEPHONE_REGEX, phone);
        if (email.isEmpty() || login.isEmpty() || phone.isEmpty() || address.isEmpty()
                || password.isEmpty() || password.length() < 6 || login.length() < 6
                || phone.length() < 6 || !emailMatch || !phoneMatch) {
            formatCheck(email, login, phone, address, password, emailMatch, phoneMatch);
        }
        else {
            validateInfo(email, phone, login, () -> {
                if (!isEmailExist && !isPhoneExist && !isLoginExist) {
                    createAccount(email, login, phone, address, password);
                    finish();
                } else {
                    if (isEmailExist)
                        emailEditText.setError(getResources().getString(R.string.email_used));
                    if (isPhoneExist)
                        phoneEditText.setError(getResources().getString(R.string.number_used));
                    if (isLoginExist)
                        loginEditText.setError(getResources().getString(R.string.login_used));
                }
            });
        }
    }

    private void createAccount(String email, String login, String phone, String address, String password) {
        final byte[] saltB = Utils.saltGenerate();
        final byte[] hashedB = Utils.hash(password.toCharArray(), saltB);
        final String salt = Base64.encodeBytes(saltB);
        final String hash = Base64.encodeBytes(hashedB);
        final StringRequest requestCreateAccount = new StringRequest(Request.Method.POST,
                Utils.CREATE_USER,
                response -> { },
                error -> System.out.println(error.getMessage())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("loginn", login);
                params.put("hash", hash);
                params.put("salt",salt);
                params.put("mail",email);
                params.put("addresss",address);
                params.put("phone",phone);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(requestCreateAccount);
    }

    private void validateInfo(String email, String phone, String login, final VolleyCallBack volleyCallBack) {
        JsonArrayRequest requestBookPublisher = new JsonArrayRequest(
                String.format(Locale.ENGLISH, Utils.EMAIL_TELEPHONE_LOGIN_CHECK, email, phone, login),
                response -> {
                    try {
                        isEmailExist = response.getJSONObject(0).getBoolean("emailexist");
                        isPhoneExist = response.getJSONObject(0).getBoolean("telephoneexist");
                        isLoginExist = response.getJSONObject(0).getBoolean("loginexist");
                        volleyCallBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> System.out.println(error.getMessage()));
        queue.add(requestBookPublisher);
    }

    private void formatCheck(String email, String login, String phone, String address,
                             String password, boolean emailMatch, boolean phoneMatch) {
        if (!emailMatch) emailEditText.setError(getResources().getString(R.string.email_regex_check));
        if (!phoneMatch) phoneEditText.setError(getResources().getString(R.string.phone_regex_check));

        if (password.length() < 6) passwordEditText.setError(getResources().getString(R.string.characters6));
        if (login.length() < 6) loginEditText.setError(getResources().getString(R.string.characters6));
        if (phone.length() < 6) phoneEditText.setError(getResources().getString(R.string.characters12));

        if (email.isEmpty()) emailEditText.setError(getResources().getString(R.string.empty));
        if (phone.isEmpty()) phoneEditText.setError(getResources().getString(R.string.empty));
        if (login.isEmpty()) loginEditText.setError(getResources().getString(R.string.empty));
        if (address.isEmpty()) addressEditText.setError(getResources().getString(R.string.empty));
        if (password.isEmpty()) passwordEditText.setError(getResources().getString(R.string.empty));
    }
}