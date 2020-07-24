package com.example.fbu_app.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.fbu_app.MainActivity;
import com.example.fbu_app.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.Arrays;

public class FBLoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_login);
        //FacebookSdk.sdkInitialize();
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            goMainActivity();
            //disconnectFromFacebook();
            return;
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //LoginManager.getInstance().logInWithReadPermissions(FBLoginActivity.this, Arrays.asList("public_profile", "user_friends"));
                if (Profile.getCurrentProfile() == null) {
                    ProfileTracker profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            this.stopTracking();
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("name", Profile.getCurrentProfile().getName());
                            Uri uri = Profile.getCurrentProfile().getProfilePictureUri(100, 100);
                            ParseFile file = new ParseFile(new File(uri.toString()));
                            currentUser.put("profileImage", file);
                            currentUser.saveInBackground();
                        }
                    };
                }
                goMainActivity();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }
}