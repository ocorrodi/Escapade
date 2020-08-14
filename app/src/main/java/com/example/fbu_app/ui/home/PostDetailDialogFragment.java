package com.example.fbu_app.ui.home;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.User;
import com.example.fbu_app.ui.profile.ProfileFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostDetailDialogFragment extends DialogFragment {

    Button exit;
    MaterialTextView tvTitle;
    MaterialTextView tvLocation;
    Post currPost;
    RelativeLayout imageLayout;
    MaterialTextView tvNotes;
    MaterialTextView tvDate;
    ImageButton ivProfile;
    ImageButton btnLike;
    ChipGroup tagChips;

    public PostDetailDialogFragment() {}

    public static PostDetailDialogFragment newInstance(Post post) {
        PostDetailDialogFragment frag = new PostDetailDialogFragment();
        Bundle bundle = new Bundle();
        //add fields to be retrieved by dialog fragment
        bundle.putString("title", post.getTitle());
        bundle.putDouble("latitude", post.getLocation().getLatitude());
        bundle.putDouble("longitude", post.getLocation().getLongitude());
        bundle.putParcelable("post", Parcels.wrap(post));
        bundle.putParcelable("images", Parcels.wrap(post.getImages()));
        bundle.putString("notes", post.getNotes());
        bundle.putString("date", post.getDate().toString());
        bundle.putString("image", post.getUser().getString("profileImageUri"));
        bundle.putParcelable("user", Parcels.wrap(post.getUser()));
        bundle.putStringArrayList("tags", (ArrayList<String>) post.getTags());
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

        this.exit = view.findViewById(R.id.btnExit);
        this.tvTitle = view.findViewById(R.id.tvItemName);
        this.tvLocation = view.findViewById(R.id.tvAddress);
        this.imageLayout = view.findViewById(R.id.rvImages);
        this.tvNotes = view.findViewById(R.id.tvNotes);
        this.tvDate = view.findViewById(R.id.tvDate);
        this.ivProfile = view.findViewById(R.id.ivProfile);
        this.btnLike = view.findViewById(R.id.btnLike);
        this.tagChips = view.findViewById(R.id.tag_chips);

        ImagesFragment imagesFrag;

        //get location of post in LatLng
        LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

        //initialize map fragment
        PostMapFragment mapFrag = new PostMapFragment(latLng, (Post) Parcels.unwrap(getArguments().getParcelable("post")));

        try {
            imagesFrag = new ImagesFragment(reverseImageType((List<ParseFile>) Parcels.unwrap(getArguments().getParcelable("images"))));
        } catch (ParseException e) {
            imagesFrag = new ImagesFragment();
            e.printStackTrace();
        }

        //load map fragment and images recycler view into corresponding layout
        getChildFragmentManager().beginTransaction().replace(R.id.location, mapFrag, mapFrag.getTag()).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.rvImages, imagesFrag, imagesFrag.getTag()).commit();

        this.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        //get current post
        this.currPost = (Post) Parcels.unwrap(getArguments().getParcelable("post"));

        //set attributes and fields on dialog fragment
        String title = getArguments().getString("title");
        this.tvTitle.setText(title);
        this.tvNotes.setText(getArguments().getString("notes"));
        this.tvLocation.setText(getAddress(getArguments().getDouble("latitude"), getArguments().getDouble("longitude")));

        arrayToChips(getArguments().getStringArrayList("tags"));

        String myFormat = "MM/dd/yy"; //format for date label
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvDate.setText("Date visited: " + sdf.format(currPost.getDate()));

        Glide.with(getContext()).load(getArguments().getString("image")).apply(RequestOptions.circleCropTransform()).into(ivProfile);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = Parcels.unwrap(getArguments().getParcelable("user"));
                getDialog().dismiss();
                getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ProfileFragment(user)).addToBackStack(null).commit();
            }
        });

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        List<Object> likedPosts = getLikedPosts();
        boolean isIn = isLiked(likedPosts, currPost);

        if (isIn) { //post has been liked already
            btnLike.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
        else { //post not liked yet
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

    public String getAddress(double lat, double lng) { //get address as a formatted string from latitude and longitude
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

    //converts list of parse files to list of files
    public List<File> reverseImageType(List<ParseFile> images) throws ParseException {
        List<File> files = new ArrayList<>();
        for (ParseFile image : images) {
            File file = image.getFile();
            files.add(file);
        }
        return files;
    }

    //get posts liked by user
    public List<Object> getLikedPosts() { return ParseUser.getCurrentUser().getList(User.KEY_LIKES); }

    //add post to those liked by user
    public void addLikedPost(Post post) {
        List<Object> likedPosts = getLikedPosts();
        if (likedPosts == null) {
            likedPosts = new ArrayList<>();
        }
        likedPosts.add((Object) post);
        ParseUser.getCurrentUser().put(User.KEY_LIKES, likedPosts);
        ParseUser.getCurrentUser().saveInBackground();
        post.addLike();
    }

    //remove post from those liked by user
    public void deleteLikedPost(final Post post) {
        List<Object> likedPosts = getLikedPosts();
        likedPosts.remove(indexToRemove(likedPosts, post));
        ParseUser.getCurrentUser().put(User.KEY_LIKES, likedPosts);
        ParseUser.getCurrentUser().saveInBackground();
        post.removeLike();
    }

    //set the posts liked by a user
    public void setLikedPosts(List<Object> posts) { ParseUser.getCurrentUser().put(User.KEY_LIKES, posts);}

    //returns bool representing whether user has liked post using object ID comparison
    public boolean isLiked(List<Object> likedPosts, Post post) {
        for (Object obj : likedPosts) {
            if (((Post) obj).getObjectId().equals(post.getObjectId())) return true;
        }
        return false;
    }

    //gets index of post to remove using object ID comparison
    public int indexToRemove(List<Object> likedPosts, Post post) {
        for (int i = 0; i < likedPosts.size(); i++) {
            if (((Post) likedPosts.get(i)).getObjectId().equals(post.getObjectId())) return i;
        }
        return 0;
    }

    private void arrayToChips(ArrayList<String> tags) {
        Chip lChip = new Chip(getContext());
        for (int i = 0; i < tags.size(); i++) {
            lChip.setText(tags.get(i));
            lChip.setTextColor(getResources().getColor(R.color.white));
            lChip.setChipBackgroundColor(getResources().getColorStateList(R.color.colorAccent));
            lChip.setCheckable(false);
            lChip.setTextIsSelectable(false);
            this.tagChips.addView(lChip, tagChips.getChildCount() - 1);
        }
    }
}