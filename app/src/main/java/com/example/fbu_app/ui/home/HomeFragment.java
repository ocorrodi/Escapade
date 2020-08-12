package com.example.fbu_app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.fbu_app.ui.dashboard.AddFragment.AUTOCOMPLETE_REQUEST_CODE;

public class HomeFragment extends Fragment {

    ArrayList<Post> posts;
    PostMapFragment mapFrag;
    PostListFragment listFrag;
    LatLng currLocation;
    final double halfWeight = .5;
    final int numPosts = 10;
    final int FIVE_MILES_IN_METERS = 8047;

    public LinearLayout layout2;
    public LinearLayout layout1;

    public FragmentManager manager;

    public LinearLayout.LayoutParams mapParams;
    public LinearLayout.LayoutParams listParams;

    OnSwipeTouchListener listener;

    public ParseUser filterUser;
    public String filterCountry;
    public String sortParam;

    public ProgressBar pbLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.manager = getFragmentManager();

        pbLoading = view.findViewById(R.id.pbLoading);

        this.layout2 = view.findViewById(R.id.map);
        this.layout1 = view.findViewById(R.id.list);

        //make the two views "split screen"
        this.mapParams = new LinearLayout.LayoutParams(layout1.getLayoutParams());
        this.listParams = new LinearLayout.LayoutParams(layout2.getLayoutParams());

        this.mapParams.weight = (float) halfWeight;

        this.layout1.setLayoutParams(this.mapParams);

        this.listParams.weight = (float) halfWeight;

        this.layout2.setLayoutParams(this.listParams);

        this.listener = new OnSwipeTouchListener(getContext(), view.findViewById(R.id.llHome));

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        this.mapFrag = new PostMapFragment();
        this.listFrag = new PostListFragment();

        manager.beginTransaction().replace(R.id.map, mapFrag, mapFrag.getTag()).commit();
        manager.beginTransaction().replace(R.id.list, listFrag, listFrag.getTag()).commit();

        posts = new ArrayList<>();

        // Initialize the SDK
        Places.initialize(getActivity().getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getContext());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                currLocation = place.getLatLng();
                mapFrag.changeFocus(currLocation);
                queryPosts(true);
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /* get the posts from Parse
     * args: boolean whether user is searching for location
     */
    protected void queryPosts(final boolean isSearch) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);

        query.setLimit(10);

        if (!isSearch) query.setLimit(numPosts);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<Post> posts2, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts");
                    Toast.makeText(getContext(), "error getting posts", Toast.LENGTH_LONG).show();
                }
                if (posts2 == null) return;
                for (Post post : posts2) {
                    Log.i(TAG, "Post: " + post.getTitle() + " username: " + post.getUser().getUsername());
                    //Toast.makeText(getContext(), "success with posts", Toast.LENGTH_LONG).show();
                }
                if (isSearch) {
                    sortPosts(posts2);
                }
                hideProgressBar();
                posts.clear();
                posts.addAll(posts2);
                listFrag.setPosts(posts2); //pass updated posts to list fragment
                mapFrag.setPosts(posts2); //pass updated posts to map fragment
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    //sort the posts according to distance from currLocation
    public void sortPosts(List<Post> posts2) {
        posts2.sort(new Comparator<Post>() {
            @Override
            public int compare(Post post, Post t1) {
                float[] results1 = new float[1];
                float[] results2 = new float[1];
                double startLat1 = post.getLocation().getLatitude();
                double startLong1 = post.getLocation().getLongitude();
                double startLat2 = t1.getLocation().getLatitude();
                double startLong2 = t1.getLocation().getLongitude();
                double endLat = currLocation.latitude;
                double endLong = currLocation.longitude;
                android.location.Location.distanceBetween(startLat1, startLong1, endLat, endLong, results1);
                android.location.Location.distanceBetween(startLat2, startLong2, endLat, endLong, results2);
                if (Math.abs(results1[0] - results2[0]) <= FIVE_MILES_IN_METERS) {
                    //locations are approximately same distance away (5 mile buffer)
                    return Integer.compare(t1.getLikes(), post.getLikes());
                }
                return Float.compare(results1[0], results2[0]);
            }
        });
        posts2.subList(0, Math.min(posts2.size(), numPosts));
    }

    public void getPosts() {
        pbLoading.setVisibility(View.VISIBLE);
        queryPosts(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                //Toast.makeText(getContext(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                //searchBar.setText(address);
                currLocation = place.getLatLng();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public static class OnSwipeTouchListener extends HomeFragment implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        Context context;
        View view;

        LinearLayout layout1;
        LinearLayout layout2;

        LinearLayout.LayoutParams listParams;
        LinearLayout.LayoutParams mapParams;

        OnSwipeTouchListener(Context ctx, View mainView) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            context = ctx;
            view = mainView;
            layout1 = view.findViewById(R.id.map);
            layout2 = view.findViewById(R.id.list);

            mapParams = new LinearLayout.LayoutParams(layout1.getLayoutParams());
            listParams = new LinearLayout.LayoutParams(layout2.getLayoutParams());

            mapParams.weight = (float) halfWeight;
            listParams.weight = (float) halfWeight;

            layout1.setLayoutParams(mapParams);

            layout2.setLayoutParams(listParams);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public class GestureListener extends
                GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        void onSwipeTop() {
            //Toast.makeText(context, "Swiped Up", Toast.LENGTH_SHORT).show();

            mapParams.weight += halfWeight;
            listParams.weight -= halfWeight;

            layout1.setLayoutParams(mapParams);

            layout2.setLayoutParams(listParams);

            this.onSwipe.swipeTop();
        }

        void onSwipeBottom() {
            //Toast.makeText(context, "Swiped Down", Toast.LENGTH_SHORT).show();

            mapParams.weight -= halfWeight;
            listParams.weight += halfWeight;

            layout1.setLayoutParams(mapParams);

            layout2.setLayoutParams(listParams);
            this.onSwipe.swipeBottom();
        }

        onSwipeListener onSwipe;

        interface onSwipeListener {
            void swipeTop();

            void swipeBottom();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final ArrayList<ParseUser> users = new ArrayList<>();
        queryUsers();
        return true;
    }

    public ArrayList<String> getCountries() {
        ArrayList<String> countries = new ArrayList<>();
        for (Post post : posts) {
            String name = getCountry(post.getLocation().getLatitude(), post.getLocation().getLongitude());
            countries.add(name);
        }
        return new ArrayList(new HashSet(countries)); //remove duplicates
    }

    public String getCountry(double lat, double lng) { //get address as a formatted string from latitude and longitude
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String country = obj.getCountryName();
            return country;
        } catch (IOException e) {
            return "";
        }
    }

    public void queryUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

        query.findInBackground(new FindCallback<ParseUser>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts");
                    //Toast.makeText(getContext(), "error getting posts", Toast.LENGTH_LONG).show();
                }
                showFilterFrag(users);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showFilterFrag(List<ParseUser> users) {
        FilterFragment filterFrag = FilterFragment.newInstance((ArrayList<ParseUser>) users, getCountries());
        filterFrag.show(getFragmentManager(), TAG);
    }

    public void startFilter(String countryName, String userName, String sortParam) {
        this.filterCountry = countryName;
        this.sortParam = sortParam;
        queryPostsWithParams(userName, filterCountry, sortParam);
    }

    protected void queryPostsWithParams(String user, String countryName, String sortParam) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);


        if (user != "Any") query.whereEqualTo("userName", user);
        if (countryName != "Any") query.whereEqualTo("country", countryName);

        if (sortParam != "Most recent" && sortParam != "None") {
            query.addDescendingOrder("likes");
        }
        else {
            query.addDescendingOrder("createdAt");
        }

        query.findInBackground(new FindCallback<Post>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<Post> posts2, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts");
                    //Toast.makeText(getContext(), "error getting posts", Toast.LENGTH_LONG).show();
                }
                for (Post post : posts2) {
                    Log.i(TAG, "Post: " + post.getTitle() + " username: " + post.getUser().getUsername());
                    //Toast.makeText(getContext(), "success with posts", Toast.LENGTH_LONG).show();
                }
                if (posts2 == null || posts2.size() == 0) posts.clear();
                posts.addAll(posts2);
                listFrag.setPosts(posts2); //pass updated posts to list fragment
                mapFrag.setPosts(posts2); //pass updated posts to map fragment
            }
        });
    }

    public void hideProgressBar() {
        pbLoading.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,0);
        pbLoading.setLayoutParams(params);
    }
}