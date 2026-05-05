package com.example.androidphotos;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import com.example.androidphotos.model.Album;
import com.example.androidphotos.model.AppData;
import com.example.androidphotos.model.Photo;
import com.example.androidphotos.storage.DataStore;

/**
 * Displays a selected photo and lets the user move backward or forward.
 */
public class PhotoActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_INDEX = "album_index";
    public static final String EXTRA_PHOTO_INDEX = "photo_index";

    private AppData appData;
    private Album album;
    private int albumIndex;
    private int photoIndex;

    private TextView photoNameTextView;
    private ImageView photoImageView;
    private Button previousButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        albumIndex = getIntent().getIntExtra(EXTRA_ALBUM_INDEX, -1);
        photoIndex = getIntent().getIntExtra(EXTRA_PHOTO_INDEX, -1);

        photoNameTextView = findViewById(R.id.text_photo_detail_name);
        photoImageView = findViewById(R.id.image_photo_detail);
        previousButton = findViewById(R.id.button_previous_photo);
        nextButton = findViewById(R.id.button_next_photo);

        previousButton.setOnClickListener(view -> {
            photoIndex--;
            bindPhoto();
        });
        nextButton.setOnClickListener(view -> {
            photoIndex++;
            bindPhoto();
        });

        loadAlbumAndBind();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAlbumAndBind();
    }

    private void loadAlbumAndBind() {
        appData = DataStore.load(this);
        if (albumIndex < 0 || albumIndex >= appData.getAlbums().size()) {
            Toast.makeText(this, R.string.error_album_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        album = appData.getAlbums().get(albumIndex);
        if (album.getPhotos().isEmpty()) {
            Toast.makeText(this, R.string.error_photo_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (photoIndex < 0) {
            photoIndex = 0;
        } else if (photoIndex >= album.getPhotos().size()) {
            photoIndex = album.getPhotos().size() - 1;
        }

        bindPhoto();
    }

    private void bindPhoto() {
        List<Photo> photos = album.getPhotos();
        if (photos.isEmpty() || photoIndex < 0 || photoIndex >= photos.size()) {
            Toast.makeText(this, R.string.error_photo_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Photo photo = photos.get(photoIndex);
        photoNameTextView.setText(photo.getDisplayName());
        photoImageView.setImageDrawable(null);
        photoImageView.setImageURI(Uri.parse(photo.getUriString()));
        if (photoImageView.getDrawable() == null) {
            photoImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        previousButton.setEnabled(photoIndex > 0);
        nextButton.setEnabled(photoIndex < photos.size() - 1);
    }
}
