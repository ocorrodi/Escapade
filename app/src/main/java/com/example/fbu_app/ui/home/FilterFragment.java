package com.example.fbu_app.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.fbu_app.R;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends DialogFragment {

    Spinner user;
    Spinner country;
    Spinner sort;
    MaterialButton btnApply;

    ArrayList<ParseUser> users;
    ArrayList<String> userNames;
    ArrayList<String> countries;
    String[] sortParams = {"Most recent", "Most likes"};

    ArrayAdapter usersAdapter;
    ArrayAdapter countriesAdapter;
    ArrayAdapter sortAdapter;


    public FilterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(ArrayList<ParseUser> parseUsers, ArrayList<String> countries) {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.user = view.findViewById(R.id.user);
        this.country = view.findViewById(R.id.country);
        this.sort = view.findViewById(R.id.sort);
        this.btnApply = view.findViewById(R.id.btnApply);

        int spinnerLayout = R.layout.spinner_item;

        mapUsers();

        this.usersAdapter = new ArrayAdapter<>(getContext(), spinnerLayout, userNames);
        this.countriesAdapter = new ArrayAdapter<>(getContext(), spinnerLayout, countries);
        this.sortAdapter = new ArrayAdapter<>(getContext(), spinnerLayout, sortParams);

        this.user.setAdapter(usersAdapter);
        this.country.setAdapter(countriesAdapter);
        this.sort.setAdapter(sortAdapter);
    }

    public void mapUsers() {
        for (ParseUser user : users) {
            userNames.add(user.getString("name"));
        }
        userNames.add(0, "all");
    }
}