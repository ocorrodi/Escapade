package com.example.fbu_app.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.R;
import com.google.android.material.textview.MaterialTextView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    final String[] titles = {"Profile", "Friends", "Liked Posts", "Logout", "Disconnect from FB", "Settings"};
    final int numItems = 6;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mtvTitle.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return numItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView mtvTitle;

        public ViewHolder(View view) {
            super(view);
            this.mtvTitle = view.findViewById(R.id.mtvTitle);
        }
    }
}