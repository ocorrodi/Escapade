package com.example.fbu_app.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.fbu_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
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
    String[] sortParams = {"None", "Most recent", "Most likes"};

    String currCountry;
    String currUserName;
    String currSortParam;

    ArrayAdapter usersAdapter;
    ArrayAdapter countriesAdapter;
    ArrayAdapter sortAdapter;

    ChipGroup tagsChipGroup;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(ArrayList<ParseUser> parseUsers, ArrayList<String> countries) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        ArrayList<Parcelable> wrappedUsers = new ArrayList<>();
        for (ParseUser user : parseUsers) {
            wrappedUsers.add(Parcels.wrap(user));
        }
        bundle.putStringArrayList("countries", countries);
        bundle.putParcelableArrayList("users", wrappedUsers);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
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
        this.tagsChipGroup = view.findViewById(R.id.chips_group);

        int spinnerLayout = R.layout.spinner_item;

        this.users = new ArrayList<>();

        unwrapUsers(getArguments().getParcelableArrayList("users"));
        this.countries = getArguments().getStringArrayList("countries");

        mapUsers();

        this.usersAdapter = new ArrayAdapter<>(getContext(), spinnerLayout, userNames);
        this.countriesAdapter = new ArrayAdapter<>(getContext(), spinnerLayout, countries);
        this.sortAdapter = new ArrayAdapter<>(getContext(), spinnerLayout, sortParams);

        this.countries.add(0, "Any");

        this.userNames.add(0, "Any");

        this.user.setAdapter(usersAdapter);
        this.country.setAdapter(countriesAdapter);
        this.sort.setAdapter(sortAdapter);

        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currCountry = countries.get(country.getSelectedItemPosition());
                currSortParam = sortParams[sort.getSelectedItemPosition()];
                currUserName = userNames.get(user.getSelectedItemPosition());

                final ArrayList<String> tags = new ArrayList<>();
                getSelectedTags(tags);

                HomeFragment homeFragment = getHomeFragment();

                homeFragment.startFilter(currCountry, currUserName, currSortParam, tags);

                getDialog().dismiss();
            }
        });
    }

    public void mapUsers() {
        userNames = new ArrayList<>();
        for (ParseUser user : users) {
            String name = user.getString("name");
            if (name != null) userNames.add(name);
        }
        userNames = new ArrayList(new HashSet(userNames)); //remove duplicates
    }

    public void unwrapUsers(ArrayList<Parcelable> wrappedUsers) {
        for (Parcelable user : wrappedUsers) {
            users.add((ParseUser) Parcels.unwrap(user));
        }
    }

    public HomeFragment getHomeFragment() {
        List<Fragment> frags = getFragmentManager().getFragments();

        for (int i = 0; i < frags.size(); i++) {
            if (frags.get(i).getClass() == HomeFragment.class) {
                return (HomeFragment) frags.get(i);
            }
        }
        return null;
    }

    public void getSelectedTags(ArrayList<String> tags) {
        for (int i = 0; i < tagsChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) tagsChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                tags.add(chip.getText().toString());
            }
        }
    }
}