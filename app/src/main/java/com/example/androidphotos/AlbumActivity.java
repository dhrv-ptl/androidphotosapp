package com.example.androidphotos;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Stub album screen for navigation from the home screen.
 */
public class AlbumActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_NAME = "album_name";
    public static final String EXTRA_ALBUM_INDEX = "album_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        String albumName = getIntent().getStringExtra(EXTRA_ALBUM_NAME);
        if (albumName == null || albumName.trim().isEmpty()) {
            albumName = getString(R.string.album_missing);
        }

        TextView albumTitle = findViewById(R.id.text_album_title);
        TextView albumMessage = findViewById(R.id.text_album_message);

        albumTitle.setText(albumName);
        albumMessage.setText(R.string.album_stub_message);
    }
}
