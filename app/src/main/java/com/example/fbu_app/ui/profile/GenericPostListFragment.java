package com.example.fbu_app.ui.profile;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.User;
import com.example.fbu_app.ui.home.PostsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GenericPostListFragment extends Fragment {

    List<Post> posts;
    PostProperty postProperty;
    PostsAdapter adapter;
    RecyclerView rvPosts;
    ParseUser user;

    public GenericPostListFragment() {
        // Required empty public constructor
    }

    public GenericPostListFragment(PostProperty property) {
        this.postProperty = property;
    }

    public static GenericPostListFragment newInstance(String param1, String param2) {
        GenericPostListFragment fragment = new GenericPostListFragment();
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
        return inflater.inflate(R.layout.fragment_generic_post_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPostList);

        posts = new ArrayList<>();

        adapter = new PostsAdapter(posts, getFragmentManager(), getContext());

        rvPosts.setAdapter(adapter);

        switch (postProperty) {
            case PERSONAL:
                queryPosts();
                break;
            case LIKED:
                getUser();
                break;
            default:
                break;
        }
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);

        if (postProperty == PostProperty.PERSONAL) query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void done(List<Post> posts2, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting posts");
                }
                for (Post post : posts2) {
                    Log.i(TAG, "Post: " + post.getTitle() + " username: " + post.getUser().getUsername());
                }
                posts.addAll(posts2);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public List<Post> convertPosts(List<Object> objects) {
        List<Post> posts = new ArrayList<>();
        for (Object obj : objects) {
            posts.add((Post) obj);
        }
        return posts;
    }
    public List<Object> getLikedPosts(ParseUser user) { return user.getList(User.KEY_LIKES); }

    public ParseUser getUser() {
        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.include(User.KEY_LIKES);
        query.include(User.KEY_LIKES.concat("." + Post.KEY_USER));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                user = objects.get(0);
                posts.addAll(convertPosts(getLikedPosts(user)));
                adapter.notifyDataSetChanged();
            }
        });
        return user;
    }
}