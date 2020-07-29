package com.example.fbu_app.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileCardViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileCardViewFragment extends Fragment {


    public ProfileCardViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileCardViewFragment newInstance(String param1, String param2) {
        ProfileCardViewFragment fragment = new ProfileCardViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_profile_cardview, container, false);
    }
}