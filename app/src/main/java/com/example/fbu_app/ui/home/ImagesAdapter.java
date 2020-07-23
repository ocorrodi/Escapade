package com.example.fbu_app.ui.home;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.parse.ParseFile;

import java.io.File;
import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {


    List<File> images;
    Context context;

    public ImagesAdapter(List<File> items, Context context) {
        images = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide.with(context).load(images.get(position)).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView ivImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivImage = view.findViewById(R.id.ivPostImage);
        }
    }
}