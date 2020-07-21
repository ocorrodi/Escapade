package com.example.fbu_app.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.ui.home.dummy.DummyContent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class PostListFragment extends Fragment {

    ArrayList<Post> posts;
    public static final String TAG = "PostListFragment";
    public PostsAdapter adapter;
    View newView;

    public PostListFragment() {
    }

    public static PostListFragment newInstance(int columnCount) {
        PostListFragment fragment = new PostListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        newView = view;

        setup();

        //HomeFragment parent = (HomeFragment) getParentFragment();

        List<Fragment> frags = getParentFragmentManager().getFragments();

        HomeFragment homeFrag = (HomeFragment) frags.get(0);

        homeFrag.getPosts();

        return view;
    }

    public void setup() {
        posts = new ArrayList<>();

        // Set the adapter
        Context context = newView.getContext();
        RecyclerView recyclerView = (RecyclerView) newView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PostsAdapter(posts, getFragmentManager(), context);
        recyclerView.setAdapter(adapter);
    }

    public void setPosts(List<Post> newPosts) {
        posts.addAll(newPosts);
        adapter.notifyDataSetChanged();
    }
}