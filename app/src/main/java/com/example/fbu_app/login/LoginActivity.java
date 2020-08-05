package com.example.fbu_app.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fbu_app.MainActivity;
import com.example.fbu_app.R;


import com.example.fbu_app.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText usernameEditText;
    TextInputEditText passwordEditText;
    MaterialButton loginButton;
    MaterialButton registerButton;
    ProgressBar loadingProgressBar;
    TextInputLayout usernameLayout;
    TextInputLayout passwordLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        if (ParseUser.getCurrentUser() != null && !(getIntent().hasExtra("loggedOut"))) {
            goFBLogin(); //go to fb login
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(username, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!checkPasswordSignup()) return;
                usernameInUse(username);
            }
        });
    }

    //sign up user by creating new Parse user with given username and password
    private void signUpUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        List<Object> empty = new ArrayList<>();
        user.put(User.KEY_LIKES, empty);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("LoginActivity", "error signing up" + e.toString());
                    Toast.makeText(LoginActivity.this, "error with signup", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(LoginActivity.this, "sign up successful", Toast.LENGTH_SHORT).show();
                goFBLogin();
            }
        });
    }

    //attempt to login user with given username and password
    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e("LoginActivity", "issue with login");
                    passwordLayout.setError("Password does not match username");
                    return;
                }
                usernameLayout.setError(null);
                passwordLayout.setError(null);
                //Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_LONG).show();
                goFBLogin();
            }
        });
    }

    //go to the FB login page
    private void goFBLogin() {
        Intent i = new Intent(this, FBLoginActivity.class);
        startActivity(i);
        finish();
    }

    public void usernameInUse(final String username) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

        query.whereEqualTo("username", username);

        query.findInBackground(new FindCallback<ParseUser>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null || users.size() > 0) {
                    usernameLayout.setError("Username is already in use");
                    return;
                }
                usernameLayout.setError(null);
                signUpUser(username, passwordEditText.getText().toString());
            }
        });
    }

    public boolean checkPasswordSignup() {
        String password = passwordEditText.getText().toString();
        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return false;
        }
        usernameLayout.setError(null);
        return true;
    }

}