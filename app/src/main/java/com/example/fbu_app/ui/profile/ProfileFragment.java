package com.example.fbu_app.ui.profile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;

public class ProfileFragment extends Fragment {

    public ImageView ivProfileImage;
    public TextView tvUsername;
    public TextView tvEmail;
    public ImageButton ibEmail;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        this.ivProfileImage = view.findViewById(R.id.ivProfileImage);
        this.tvUsername = view.findViewById(R.id.tvUsername);
        this.tvEmail = view.findViewById(R.id.tvEmail);
        this.ibEmail = view.findViewById(R.id.ibEmail);

        Glide.with(getContext()).load(ParseUser.getCurrentUser().getString("profileImageUri")).apply(RequestOptions.circleCropTransform()).into(ivProfileImage);

        this.tvUsername.setVisibility(View.VISIBLE);

        //get user's profile attributes
        this.tvUsername.setText(ParseUser.getCurrentUser().getString("name"));

        this.tvEmail.setText(ParseUser.getCurrentUser().getString("email2"));

        ibEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
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

    protected void sendEmail() {
        String[] TO = {tvEmail.getText().toString()};
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, TO);
        final PackageManager pm = getContext().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") ||
                    info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
        if (best != null)
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
            getActivity().finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}