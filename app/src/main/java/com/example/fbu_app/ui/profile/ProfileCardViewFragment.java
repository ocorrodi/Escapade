package com.example.fbu_app.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_app.R;
import com.google.android.material.card.MaterialCardView;

public class ProfileCardViewFragment extends Fragment {

    RecyclerView rvProfileItems;
    final int NUM_COLUMNS = 2;

    public ProfileCardViewFragment() {
        // Required empty public constructor
    }

    public static ProfileCardViewFragment newInstance(String param1, String param2) {
        ProfileCardViewFragment fragment = new ProfileCardViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_cardview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvProfileItems = view.findViewById(R.id.rv_profile);
        rvProfileItems.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));
        rvProfileItems.setAdapter(new ProfileAdapter());
    }
}