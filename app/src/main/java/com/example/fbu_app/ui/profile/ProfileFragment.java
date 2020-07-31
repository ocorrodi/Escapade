package com.example.fbu_app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbu_app.R;
import com.example.fbu_app.login.FBLoginActivity;
import com.example.fbu_app.login.LoginActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvEmail;
    public Button btnLogout;
    public Button btnFB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout posts = view.findViewById(R.id.posts);

        this.ivProfileImage = view.findViewById(R.id.ivProfileImage);
        this.tvUsername = view.findViewById(R.id.tvUsername);
        this.tvEmail = view.findViewById(R.id.tvEmail);
        this.btnLogout = view.findViewById(R.id.btnLogout);
        this.btnFB = view.findViewById(R.id.btnFB);

        Glide.with(getContext()).load(ParseUser.getCurrentUser().getString("profileImageUri")).apply(RequestOptions.circleCropTransform()).into(ivProfileImage);

        this.tvUsername.setVisibility(View.VISIBLE);

        //get user's profile attributes
        this.tvUsername.setText(ParseUser.getCurrentUser().getString("name"));

        this.tvEmail.setText(ParseUser.getCurrentUser().getString("email2"));

        posts.setOrientation(LinearLayout.HORIZONTAL);

        btnFB.setVisibility(View.INVISIBLE);
        btnLogout.setVisibility(View.INVISIBLE);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                disconnectFromFacebook();
                goLogin();
            }
        });

        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectFromFacebook();
            }
        });
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
                goFBLogin();

            }
        }).executeAsync();
    }

    public void goFBLogin() {
        Intent i = new Intent(getContext(), FBLoginActivity.class);
        startActivity(i);
    }

    public void goLogin() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }
}