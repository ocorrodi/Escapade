package com.example.fbu_app.ui.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

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
        final FragmentManager manager = getFragmentManager();

        Button button = view.findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDetailDialogFragment newFrag = PostDetailDialogFragment.newInstance();
                newFrag.show(manager, "fragment_post_detail");
            }
        });

        final RelativeLayout layout2 = view.findViewById(R.id.map);
        final RelativeLayout layout1 = view.findViewById(R.id.list);

        //Button button = view.findViewById(R.id.button);
        //Button button1 = view.findViewById(R.id.button2);

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(layout1.getLayoutParams());
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(layout2.getLayoutParams());

        mapParams.weight = 0;

        //layout1.setLayoutParams(mapParams);

        listParams.weight = 1;

        //layout2.setLayoutParams(listParams);

        PostMapFragment mapFrag = new PostMapFragment();
        PostListFragment listFrag = new PostListFragment();

        manager.beginTransaction().replace(R.id.map, mapFrag, mapFrag.getTag()).commit();
        manager.beginTransaction().replace(R.id.list, listFrag, listFrag.getTag()).commit();


    }
}