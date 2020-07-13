package com.example.fbu_app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout layout1 = view.findViewById(R.id.map);
        RelativeLayout layout2 = view.findViewById(R.id.list);

        FragmentManager manager = getFragmentManager();

        PostMapFragment mapFrag = new PostMapFragment();
        PostListFragment listFrag = new PostListFragment();

        manager.beginTransaction().replace(R.id.map, mapFrag, mapFrag.getTag()).commit();
        manager.beginTransaction().replace(R.id.list, listFrag, listFrag.getTag()).commit();

    }
}