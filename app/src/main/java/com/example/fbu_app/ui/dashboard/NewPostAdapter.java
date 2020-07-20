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
        public final String APP_TAG = "MyCustomApp";
        public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
        public String photoFileName = "photo.jpg";
        File photoFile;

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
            launchCamera();
        }

        public void launchCamera() {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a File reference for future access
            photoFile = getPhotoFileUri(photoFileName);

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            Uri fileProvider = FileProvider.getUriForFile(context, "com.codepath.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                // Start the image capture intent to take photo
                ((MainActivity) context).startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
        // Returns the File for a photo stored on disk given the fileName
        public File getPhotoFileUri(String fileName) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

            return file;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // by this point we have the camera photo on disk
                    images.add(0, photoFile);
                    notifyDataSetChanged();
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview

                } else { // Result was a failure
                    Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}