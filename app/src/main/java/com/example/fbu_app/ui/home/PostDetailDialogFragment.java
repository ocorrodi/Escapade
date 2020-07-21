package com.example.fbu_app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fbu_app.Post;
import com.example.fbu_app.R;

public class PostDetailDialogFragment extends DialogFragment {

    public PostDetailDialogFragment() {}

    public static PostDetailDialogFragment newInstance(Post post) {
        PostDetailDialogFragment frag = new PostDetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", post.getTitle());
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button exit = view.findViewById(R.id.btnExit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        TextView textView = view.findViewById(R.id.tvItemName);
        String title = getArguments().getString("title");
        textView.setText(title);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}