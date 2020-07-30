package com.example.fbu_app.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

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
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        //already logged in, go directly to home screen
        if (isLoggedIn) {
            goMainActivity();
            return;
        }

        //facebook login button
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", EMAIL));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                //get user profile
                if (Profile.getCurrentProfile() == null) {
                    ProfileTracker profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            this.stopTracking();
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.put("name", Profile.getCurrentProfile().getName());
                            Uri uri = Profile.getCurrentProfile().getProfilePictureUri(100, 100);
                            currentUser.put("profileImageUri", uri.toString());
                            getInfo(loginResult, currentUser);
                        }
                    };
                }
                //go to home screen
                goMainActivity();
            }

            @Override
            public void onCancel() {
                // TODO: add error handling
            }

            @Override
            public void onError(FacebookException exception) {
                // TODO: add error handling
            }
        });
    }

    //go to home screen
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

    //logout user from Facebook
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

    //get user info from FB and upload to profile in Parse
    public void getInfo(LoginResult loginResult, final ParseUser currUser) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        // Application code
                        try {
                            String email = object.getString("email");
                            currUser.put("email2", email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        currUser.saveInBackground();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }
}