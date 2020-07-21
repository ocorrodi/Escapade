package com.example.fbu_app.ui.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    ArrayList<Post> posts;
    PostMapFragment mapFrag;
    PostListFragment listFrag;

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
                //PostDetailDialogFragment newFrag = PostDetailDialogFragment.newInstance();
                //newFrag.show(manager, "fragment_post_detail");
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

        mapFrag = new PostMapFragment();
        listFrag = new PostListFragment();

        manager.beginTransaction().replace(R.id.map, mapFrag, mapFrag.getTag()).commit();
        manager.beginTransaction().replace(R.id.list, listFrag, listFrag.getTag()).commit();

        posts = new ArrayList<>();
        //queryPosts();
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.setLimit(10);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts2, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts");
                }
                for (Post post : posts2) {
                    Log.i(TAG, "Post: " + post.getTitle() + " username: " + post.getUser().getUsername());
                }
                posts.addAll(posts2);
                listFrag.setPosts(posts2);
            }
        });
    }

    public void getPosts() {
        queryPosts();
    }
}