package com.example.fbu_app.ui.home;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textview.MaterialTextView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static com.example.fbu_app.User.KEY_LIKES;

public class PostDetailDialogFragment extends DialogFragment {

    Button exit;
    MaterialTextView tvTitle;
    MaterialTextView tvLocation;
    GoogleMap map;
    Post currPost;
    RelativeLayout imageLayout;
    MaterialTextView tvNotes;
    MaterialTextView tvDate;
    ImageView ivProfile;
    ImageButton btnLike;

    public PostDetailDialogFragment() {}

    public static PostDetailDialogFragment newInstance(Post post) {
        PostDetailDialogFragment frag = new PostDetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", post.getTitle());
        bundle.putDouble("latitude", post.getLocation().getLatitude());
        bundle.putDouble("longitude", post.getLocation().getLongitude());
        bundle.putParcelable("post", Parcels.wrap(post));
        bundle.putParcelable("images", Parcels.wrap(post.getImages()));
        bundle.putString("notes", post.getNotes());
        bundle.putString("date", post.getDate().toString());
        bundle.putString("image", post.getUser().getString("profileImageUri"));
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        exit = view.findViewById(R.id.btnExit);
        tvTitle = view.findViewById(R.id.tvItemName);
        tvLocation = view.findViewById(R.id.tvAddress);
        imageLayout = view.findViewById(R.id.rvImages);
        tvNotes = view.findViewById(R.id.tvNotes);
        tvDate = view.findViewById(R.id.tvDate);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnLike = view.findViewById(R.id.btnLike);

        ImagesFragment imagesFrag;

        LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

        PostMapFragment mapFrag = new PostMapFragment(latLng, (Post) Parcels.unwrap(getArguments().getParcelable("post")));

        try {
            imagesFrag = new ImagesFragment(reverseImageType((List<ParseFile>) Parcels.unwrap(getArguments().getParcelable("images"))));
        } catch (ParseException e) {
            imagesFrag = new ImagesFragment();
            e.printStackTrace();
        }

        getChildFragmentManager().beginTransaction().replace(R.id.location, mapFrag, mapFrag.getTag()).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.rvImages, imagesFrag, imagesFrag.getTag()).commit();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        currPost = (Post) Parcels.unwrap(getArguments().getParcelable("post"));

        String title = getArguments().getString("title");
        tvTitle.setText(title);
        tvNotes.setText(getArguments().getString("notes"));
        tvLocation.setText(getAddress(getArguments().getDouble("latitude"), getArguments().getDouble("longitude")));

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvDate.setText("Date visited: " + sdf.format(currPost.getDate()));

        Glide.with(getContext()).load(getArguments().getString("image")).apply(RequestOptions.circleCropTransform()).into(ivProfile);

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        List<Object> likedPosts = getLikedPosts();
        boolean isIn = isLiked(likedPosts, currPost);
        if (isIn) {
            btnLike.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
        else {
            btnLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Object> likedPosts = getLikedPosts();
                boolean isIn = isLiked(likedPosts, currPost);
                if (!isIn) {
                    addLikedPost(currPost);
                    btnLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                    return;
                }
                deleteLikedPost(currPost);
                btnLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        });
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = "";
            String locality = obj.getLocality();
            String admin = obj.getAdminArea();
            String country = obj.getCountryName();
            if (locality != null) add += locality + ", ";
            if (admin != null) add += admin + ", ";
            if (country != null) add += country;

            Log.v("IGA", "Address" + add);

            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }
    public List<ParseFile> changeImageType(List<File> images) {
        List<ParseFile> parseFiles = new ArrayList<>();
        for (File image : images) {
            ParseFile file = new ParseFile(image);
            parseFiles.add(file);
        }
        return parseFiles;
    }

    public List<File> reverseImageType(List<ParseFile> images) throws ParseException {
        List<File> files = new ArrayList<>();
        for (ParseFile image : images) {
            File file = image.getFile();
            files.add(file);
        }
        return files;
    }

    public List<Object> getLikedPosts() { return ParseUser.getCurrentUser().getList(KEY_LIKES); }

    public void addLikedPost(Post post) {
        List<Object> likedPosts = getLikedPosts();
        if (likedPosts == null) {
            likedPosts = new ArrayList<>();
        }
        likedPosts.add((Object) post);
        ParseUser.getCurrentUser().put(KEY_LIKES, likedPosts);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public void deleteLikedPost(final Post post) {
        List<Object> likedPosts = getLikedPosts();
        likedPosts.remove(indexToRemove(likedPosts, post));
        ParseUser.getCurrentUser().put(KEY_LIKES, likedPosts);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public void setLikedPosts(List<Object> posts) { ParseUser.getCurrentUser().put(KEY_LIKES, posts);}

    public boolean isLiked(List<Object> likedPosts, Post post) {
        for (Object obj : likedPosts) {
            if (((Post) obj).getObjectId().equals(post.getObjectId())) return true;
        }
        return false;
    }

    public int indexToRemove(List<Object> likedPosts, Post post) {
        for (int i = 0; i < likedPosts.size(); i++) {
            if (((Post) likedPosts.get(i)).getObjectId().equals(post.getObjectId())) return i;
        }
        return 0;
    }
}