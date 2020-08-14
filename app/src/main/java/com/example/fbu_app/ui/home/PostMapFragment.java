package com.example.fbu_app.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.ui.dashboard.AddFragment;
import com.example.fbu_app.ui.profile.ProfileFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class PostMapFragment extends Fragment implements GoogleMap.OnMapLongClickListener {

    public List<Post> posts;
    public GoogleMap googleMap;
    public HomeFragment homeFrag;
    public LatLng location;
    public Post post;

    public PostMapFragment() {}

    public PostMapFragment(LatLng location, Post newPost) {
        this.location = location;
        post = newPost;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            loadMap(googleMap);
            if (location != null) {
                placeMarker(location, post);
                changeFocus(location);
                UiSettings settings = googleMap.getUiSettings();
                settings.setZoomControlsEnabled(true);
            }
        }

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.supportMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        /* get access to the Home Fragment for communication about loading posts
         * Home Fragment serves as main point of contact between PostListFragment and
         * PostMapFragment for synchronizing the retrieval and display of posts
         */
        List<Fragment> frags = getParentFragmentManager().getFragments();

        for (int i = 0; i < frags.size(); i++) {
            if (frags.get(i).getClass() == HomeFragment.class) {
                homeFrag = (HomeFragment) frags.get(i);
            }
        }

    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    int delay = 15;
                    handler.postDelayed(this, delay);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }
    public void setPosts(List<Post> newPosts) {
        if (newPosts.size() >= 0) posts.clear();
        posts.addAll(newPosts);
        if (googleMap != null) {
            updateMap();
        }
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
    }

    private void updateMap() {
        googleMap.clear();
        for (Post post : posts) {

            LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());

            placeMarker(point, post);

            // Animate marker using drop effect
            // --> Call the dropPinEffect method here
        }
    }
    protected void loadMap(GoogleMap googleMap2) {
        googleMap = googleMap2;
        if (googleMap != null) {
            // Map is ready
            //Toast.makeText(getContext(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    PostDetailDialogFragment newFrag = PostDetailDialogFragment.newInstance((Post) marker.getTag());
                    newFrag.show(getFragmentManager(), "fragment_post_detail");
                    return false;
                }
            });
        } else {
            //Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
        if (homeFrag != null) homeFrag.getPosts();
        googleMap.setOnMapLongClickListener(this);
    }

    //have map's camera focus on a new location
    public void changeFocus(LatLng newLocation) {
        int zoom = 5;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, zoom));
    }

    //place marker on the map
    public void placeMarker(LatLng latLng, Post post) {
        Marker marker = this.googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(post.getTitle()));
        marker.setTag(post);
        MarkerOptions marker1 = createMarker(getContext(), latLng, post.getTags().get(0));
        //marker.setIcon(createMarker(getContext(), latLng, post.getTags().get(0)));
        //googleMap.addMarker(marker1);
        dropPinEffect(marker);
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new AddFragment(latLng)).addToBackStack(null).commit();
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    private void arrayToChips(List<String> tags, ChipGroup tagChipGroup) {
        tagChipGroup.removeAllViews();
        Chip chip =
                (Chip) getLayoutInflater().inflate(R.layout.item_chip, tagChipGroup, false);
        for (int i = 0; i < tags.size(); i++) {
            chip.setText(tags.get(i));
            tagChipGroup.addView(chip, tagChipGroup.getChildCount() - 1);
        }
    }

    public static MarkerOptions createMarker(Context context, LatLng point, String bedroomCount) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(point);
        View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_item, null);
        markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int px = getHeight(bedroomCount)*2;
        markerView.layout(0, 0, px, 50);
        markerView.buildDrawingCache();
        TextView bedNumberTextView = (TextView)markerView.findViewById(R.id.textView2);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        bedNumberTextView.setText(bedroomCount);
        markerView.draw(canvas);
        marker.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
        return marker;
    }

    public static int getHeight(String str) {

        Paint paint = new Paint();
        paint.setTextSize(20);
        //Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "Helvetica.ttf");
        paint.setTypeface(Typeface.create("roboto",Typeface.BOLD));
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        Rect result = new Rect();
        paint.getTextBounds(str, 0, str.length(), result);
        return result.width();
    }
}