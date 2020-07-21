package com.example.fbu_app.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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

        posts = new ArrayList<>();

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PostsAdapter(posts, getFragmentManager(), context);
        recyclerView.setAdapter(adapter);
        queryPosts();
        return view;
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
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getTitle() + " username: " + post.getUser().getUsername());
                }
                posts.addAll(posts2);
                adapter.notifyDataSetChanged();
            }
        });
    }
}