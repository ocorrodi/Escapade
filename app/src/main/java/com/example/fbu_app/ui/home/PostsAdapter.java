package com.example.fbu_app.ui.home;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.Post;
import com.example.fbu_app.R;
import com.example.fbu_app.ui.home.dummy.DummyContent;
import com.example.fbu_app.ui.home.dummy.DummyContent.DummyItem;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final List<Post> posts;
    FragmentManager manager;
    Context context;

    public PostsAdapter(List<Post> posts, FragmentManager manager, Context context) {
        this.posts = posts;
        this.manager = manager;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post newPost = posts.get(position);
        holder.tvTitle.setText(newPost.getTitle());
        holder.tvLocation.setText(newPost.getLocation().toString());

        try {
            Glide.with(context).load(newPost.getUser().getParseFile("profileImage").getFile()).into(holder.ivImage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View mView;
        public TextView tvTitle;
        public TextView tvLocation;
        public ImageView ivImage;
        public Post post;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvTitle = view.findViewById(R.id.tvTitle);
            tvLocation = view.findViewById(R.id.tvLocation);
            ivImage = view.findViewById(R.id.ivImage);
            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            post = posts.get(position);
            PostDetailDialogFragment newFrag = PostDetailDialogFragment.newInstance(post);
            newFrag.show(manager, "fragment_post_detail");
        }
    }
}