package com.example.androidphotos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import com.example.androidphotos.model.Album;
import com.example.androidphotos.model.AppData;
import com.example.androidphotos.model.Photo;
import com.example.androidphotos.model.Tag;
import com.example.androidphotos.storage.DataStore;
import com.example.androidphotos.util.ImageLoader;

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
    private ArrayAdapter<String> tagAdapter;
    private final List<String> tagItems = new ArrayList<>();
    private Button previousButton;
    private Button nextButton;
    private Photo currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        albumIndex = getIntent().getIntExtra(EXTRA_ALBUM_INDEX, -1);
        photoIndex = getIntent().getIntExtra(EXTRA_PHOTO_INDEX, -1);

        photoNameTextView = findViewById(R.id.text_photo_detail_name);
        photoImageView = findViewById(R.id.image_photo_detail);
        Button addTagButton = findViewById(R.id.button_add_tag);
        ListView tagListView = findViewById(R.id.list_tags);
        TextView emptyTagsView = findViewById(R.id.text_empty_tags);
        previousButton = findViewById(R.id.button_previous_photo);
        nextButton = findViewById(R.id.button_next_photo);

        tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagItems);
        tagListView.setAdapter(tagAdapter);
        tagListView.setEmptyView(emptyTagsView);

        addTagButton.setOnClickListener(view -> showTagTypeDialog());
        tagListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteTagDialog(position);
            return true;
        });
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
        currentPhoto = photo;
        photoNameTextView.setText(photo.getDisplayName());
        ImageLoader.loadLargeImage(this, photoImageView, photo.getUriString());
        refreshTagList(photo);

        previousButton.setEnabled(photoIndex > 0);
        nextButton.setEnabled(photoIndex < photos.size() - 1);
    }

    private void refreshTagList(Photo photo) {
        tagItems.clear();
        for (Tag tag : photo.getTags()) {
            tagItems.add(formatTag(tag));
        }
        tagAdapter.notifyDataSetChanged();
    }

    private void showTagTypeDialog() {
        if (currentPhoto == null) {
            return;
        }

        final String[] tagTypes = {Tag.TYPE_PERSON, Tag.TYPE_LOCATION};
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_tag)
                .setItems(tagTypes, (dialog, which) -> showTagValueDialog(tagTypes[which]))
                .show();
    }

    private void showTagValueDialog(String tagType) {
        final EditText input = new EditText(this);
        input.setHint(R.string.tag_value_hint);
        input.setSingleLine(true);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.add_tag_type_title, tagType))
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.add, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String tagValue = input.getText().toString();
                    if (tagValue == null || tagValue.trim().isEmpty()) {
                        input.setError(getString(R.string.error_tag_blank));
                        return;
                    }

                    boolean added = currentPhoto.addTag(tagType, tagValue.trim());
                    if (!added) {
                        input.setError(getString(R.string.error_tag_duplicate));
                        return;
                    }

                    persistCurrentState();
                    dialog.dismiss();
                }));

        dialog.show();
    }

    private void showDeleteTagDialog(int tagIndex) {
        if (currentPhoto == null) {
            return;
        }

        List<Tag> tags = currentPhoto.getTags();
        if (tagIndex < 0 || tagIndex >= tags.size()) {
            return;
        }

        Tag tag = tags.get(tagIndex);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_tag)
                .setMessage(getString(R.string.delete_tag_message, formatTag(tag)))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    currentPhoto.removeTag(tag.getType(), tag.getValue());
                    persistCurrentState();
                })
                .show();
    }

    private void persistCurrentState() {
        try {
            DataStore.save(this, appData);
            loadAlbumAndBind();
        } catch (IllegalStateException exception) {
            Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTag(Tag tag) {
        return tag.getType() + ": " + tag.getValue();
    }
}
