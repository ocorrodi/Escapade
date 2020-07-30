package com.example.fbu_app.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.fbu_app.BuildConfig;
import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.ui.home.HomeFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseFileUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    TextInputEditText etTitle;
    TextInputEditText etDate;
    TextInputLayout etDateLayout;
    TextInputEditText etNotes;
    TextInputEditText etLocation;
    TextInputLayout etLocationLayout;
    final Calendar myCalendar = Calendar.getInstance();
    public static final int AUTOCOMPLETE_REQUEST_CODE = 42;
    public static final String TAG = "AddFragment";
    LatLng latlng;
    MaterialButton btnSubmit;
    Date date;
    NewPostFragment newPostFrag;
    String locationName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager manager = getFragmentManager();

        NewPostFragment frag = new NewPostFragment();

        this.newPostFrag = frag;

        //load the images recycler view into the layout
        manager.beginTransaction().replace(R.id.addStuff, frag, frag.getTag()).commit();

        this.etTitle = view.findViewById(R.id.etTitle);
        this.etDate = view.findViewById(R.id.etDate);
        this.etDate.setInputType(InputType.TYPE_NULL);
        this.etLocation = view.findViewById(R.id.etLocation);
        this.btnSubmit = view.findViewById(R.id.btnSubmit);
        this.etNotes = view.findViewById(R.id.etNotes);
        this.etDateLayout = view.findViewById(R.id.etDateLayout);
        this.etLocationLayout = view.findViewById(R.id.etLocationLayout);

        // Initialize the SDK
        Places.initialize(getActivity().getApplicationContext(), getResources().getString(R.string.google_maps_key));

        (this.etDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                selectDate();
            }
        });

        this.etDateLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });


        this.etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchCalled();
            }
        });

        this.etLocationLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchCalled();
            }
        });

        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseGeoPoint geoPoint = new ParseGeoPoint();
                geoPoint.setLatitude(latlng.latitude);
                geoPoint.setLongitude(latlng.longitude);
                savePost(etTitle.getText().toString(), ParseUser.getCurrentUser(), geoPoint, myCalendar.getTime());
            }
        });
    }

    //update the date label
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etDate.setText(sdf.format(myCalendar.getTime()));
        date = myCalendar.getTime();
    }

    //user clicked on search bar
    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                AddressComponents ac = place.getAddressComponents();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(getContext(), "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                this.locationName = address;
                this.etLocation.setText(address);
                this.latlng = place.getLatLng();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    //save a post into Parse with given attributes
    private void savePost(final String title, ParseUser currentUser, ParseGeoPoint location, Date date) {
        Post post = new Post();
        post.setTitle(title);
        post.setUser(currentUser);
        post.setLocation(location);
        post.setDate(date);
        post.setNotes(this.etNotes.getText().toString());
        post.setPlace(this.locationName);
        List<File> images = this.newPostFrag.getImages();
        int startIndex = 0;
        int endIndex = images.size() - 1;
        post.setImages(changeImageType(images.subList(startIndex, endIndex)));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error saving post", Toast.LENGTH_LONG).show();
                }
                Log.i(TAG, "post save was successful");
                etDate.setText("");
                etLocation.setText("");
                etTitle.setText("");
                newPostFrag.clearImages();
                etNotes.setText("");
            }
        });
    }

    //change list of Files into list of ParseFiles
    public List<ParseFile> changeImageType(List<File> images) {
        List<ParseFile> parseFiles = new ArrayList<>();
        for (File image : images) {
            ParseFile file = new ParseFile(image);
            parseFiles.add(file);
        }
        return parseFiles;
    }

    //select date from date picker
    public void selectDate() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
