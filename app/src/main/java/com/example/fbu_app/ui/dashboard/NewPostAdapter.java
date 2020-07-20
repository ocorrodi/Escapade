package com.example.fbu_app.ui.dashboard;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;

import java.io.File;
import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class NewPostAdapter extends RecyclerView.Adapter<NewPostAdapter.ViewHolder> {

    private final List<File> images;

    public NewPostAdapter(List<File> items) {
        images = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_new_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.image = images.get(position);
        holder.newImage.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
        //Glide.with(holder.context).load(holder.image).into(holder.newImage);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View mView;
        public File image;
        public final ImageView newImage;
        public Context context;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            context = view.getContext();
            newImage = (ImageView) view.findViewById(R.id.ivNewImage);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != (images.size() - 1)) return;
            Uri newPhoto = getPhoto();
            images.add(0, new File(String.valueOf(newPhoto)));
            notifyDataSetChanged();
        }
    }
}