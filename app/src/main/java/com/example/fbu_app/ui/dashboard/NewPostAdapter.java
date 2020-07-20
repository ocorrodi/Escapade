package com.example.fbu_app.ui.dashboard;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fbu_app.MainActivity;
import com.example.fbu_app.R;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class NewPostAdapter extends RecyclerView.Adapter<NewPostAdapter.ViewHolder> {

    private final List<File> images;
    public Context newPostContext;

    public NewPostAdapter(List<File> items, Context context) {
        images = items;
        newPostContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_new_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0) {
            holder.newImage.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
            return;
        }
        holder.image = images.get(position);
        Glide.with(holder.context).load(holder.image).into(holder.newImage);

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
            context = newPostContext;
            newImage = (ImageView) view.findViewById(R.id.ivNewImage);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != (images.size() - 1)) return;
        }
    }
}