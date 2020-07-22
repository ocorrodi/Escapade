package com.example.fbu_app.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class PostMapFragment extends Fragment {

    public List<Post> posts;
    public GoogleMap googleMap;
    public HomeFragment homeFrag;
    public LatLng location;
    public String title;

    public PostMapFragment() {}

    public PostMapFragment(LatLng location, String title) {
        this.location = location;
        this.title = title;
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            loadMap(googleMap);
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }
    public void setPosts(List<Post> newPosts) {
        if (newPosts.size() > 0) posts.clear();
        posts.addAll(newPosts);
        if (googleMap != null) {
            updateMap();
        }
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
    }

    private void updateMap() {
        for (Post post : posts) {

            LatLng point = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());

            Marker marker = googleMap.addMarker(new MarkerOptions().position(point)
                    .title(post.getTitle()));

            // Animate marker using drop effect
            // --> Call the dropPinEffect method here
            dropPinEffect(marker);
        }
    }
    protected void loadMap(GoogleMap googleMap2) {
        googleMap = googleMap2;
        if (googleMap != null) {
            // Map is ready
            Toast.makeText(getContext(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
        if (homeFrag != null) homeFrag.getPosts();

        if (location != null) {
            changeFocus(location);
            placeMarker(location, title);
        }

    }

    public void changeFocus(LatLng newLocation) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
        for (int i = 0; i < 100; i++) {
            googleMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }

    public void placeMarker(LatLng latLng, String title) {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .title(title));
        dropPinEffect(marker);
    }
}