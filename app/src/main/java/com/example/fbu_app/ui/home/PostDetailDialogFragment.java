package com.example.fbu_app.ui.home;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentContainer;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostDetailDialogFragment extends DialogFragment {

    Button exit;
    TextView tvTitle;
    TextView tvLocation;
    GoogleMap map;
    Post post;

    public PostDetailDialogFragment() {}

    public static PostDetailDialogFragment newInstance(Post post) {
        PostDetailDialogFragment frag = new PostDetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", post.getTitle());
        bundle.putDouble("latitude", post.getLocation().getLatitude());
        bundle.putDouble("longitude", post.getLocation().getLongitude());
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

        exit = view.findViewById(R.id.btnExit);
        tvTitle = view.findViewById(R.id.tvItemName);
        tvLocation = view.findViewById(R.id.tvAddress);

        LatLng latLng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));

        PostMapFragment mapFrag = new PostMapFragment(latLng, getArguments().getString("title"));

        getChildFragmentManager().beginTransaction().replace(R.id.location, mapFrag, mapFrag.getTag()).commit();

        //mapFrag.changeFocus(latLng);

        //mapFrag.placeMarker(latLng, getArguments().getString("title"));

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        String title = getArguments().getString("title");
        tvTitle.setText(title);
        tvLocation.setText(getAddress(getArguments().getDouble("latitude"), getArguments().getDouble("longitude")));

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();

            Log.v("IGA", "Address" + add);

            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }
}