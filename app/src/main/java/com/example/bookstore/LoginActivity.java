package com.example.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookstore.Model.User;
import com.example.bookstore.SingletonClasses.QueueSingleton;
import com.example.bookstore.SingletonClasses.SharedPrefManager;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private int userID;
    private EditText login, password;
    private RequestQueue queue;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        isTokenExpired();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                tryLogin();
                break;
            case R.id.register_text_button:
                final Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void tryLogin() {
        String loginText = login.getText().toString();
        String passwordText = password.getText().toString();
        if (loginText.isEmpty() || passwordText.isEmpty() || loginText.length() < 6
                || passwordText.length() < 6) {
            validateData(loginText, passwordText);
        }
        else {
            final JsonArrayRequest loginRequest = new JsonArrayRequest(
                    String.format(Utils.GET_USER_PASSWORD, login.getText().toString()),
                    response -> {
                        try {
                            final char[] passwordInput = password.getText().toString().toCharArray();
                            final String salt = response.getJSONObject(0).getString("password_salt");
                            final String excepted = response.getJSONObject(0).getString("password_hash");
                            userID = response.getJSONObject(0).getInt("id");
                            final byte[] salt2 = org.postgresql.util.Base64.decode(salt);
                            final String actual = org.postgresql.util.Base64.encodeBytes(Utils.hash(passwordInput, salt2));
                            if (!excepted.equals(actual)) wrongData();
                            else tokenRequest();
                        } catch (JSONException e) {
                            wrongData();
                        }
                    },
                    error -> System.out.println(error.getMessage())
            );
            queue.add(loginRequest);
        }
    }

    private void validateData(String loginText, String passwordText) {
        if (loginText.length() < 6) login.setError(getResources().getString(R.string.characters6));
        if (passwordText.length() < 6) password.setError(getResources().getString(R.string.characters6));
        if (loginText.isEmpty()) login.setError(getResources().getString(R.string.empty));
        if (passwordText.isEmpty()) password.setError(getResources().getString(R.string.empty));
    }

    private void tokenRequest() {
        final JsonArrayRequest tokenRequest = new JsonArrayRequest(
                String.format(Utils.GET_TOKEN, login.getText().toString()),
                response -> {
                    try {
                        final String token = response.getJSONObject(0).getString("token");
                        final User user = new User(userID, login.getText().toString(), token);
                        sharedPrefManager.saveUser(user);
                        final Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> System.out.println(error.getMessage())
        );
        queue.add(tokenRequest);
    }

    private void wrongData(){
        login.getText().clear();
        password.getText().clear();
        final Toast toast = Toast.makeText(
                this,
                R.string.wrong_login_or_password,
                Toast.LENGTH_SHORT
        );
        toast.show();
    }

    private void isTokenExpired() {
        if (sharedPrefManager.isLoggedIn()) {
            final String t = sharedPrefManager.getUser().getToken();
            final String[] arr = Utils.decodeJWT(t)
                    .replace(Utils.RIGHT_BRACE ,Utils.EMPTY_CHARACTER).split(Utils.COLON_CHARACTER);
            final long timeNow = System.currentTimeMillis() / 1000;
            if (timeNow < Long.parseLong(arr[3])) {
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else sharedPrefManager.clear();
        }
    }

    private void init() {
        queue = QueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        sharedPrefManager = SharedPrefManager.getInstance(this);
        final Button loginButton = findViewById(R.id.loginButton);
        final TextView registerTextButton = findViewById(R.id.register_text_button);
        registerTextButton.setOnClickListener(this);
        login = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextPassword);
        loginButton.setOnClickListener(this);
    }
}