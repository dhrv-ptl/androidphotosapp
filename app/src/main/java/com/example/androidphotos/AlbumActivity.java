package com.example.androidphotos;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import com.example.androidphotos.model.Album;
import com.example.androidphotos.model.AppData;
import com.example.androidphotos.model.Photo;
import com.example.androidphotos.storage.DataStore;

/**
 * Shows photos in a selected album and supports add/remove/display actions.
 */
public class AlbumActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_NAME = "album_name";
    public static final String EXTRA_ALBUM_INDEX = "album_index";

    private AppData appData;
    private Album album;
    private int albumIndex;
    private TextView albumTitle;
    private PhotoListAdapter photoListAdapter;
    private final List<Photo> photoItems = new ArrayList<>();

    private final ActivityResultLauncher<Intent> pickPhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }

                Uri selectedUri = result.getData().getData();
                if (selectedUri == null) {
                    Toast.makeText(this, R.string.error_photo_pick_failed, Toast.LENGTH_SHORT).show();
                    return;
                }

                tryTakePersistablePermission(result.getData(), selectedUri);

                String displayName = resolveDisplayName(selectedUri);
                boolean added = album.addPhoto(new Photo(selectedUri.toString(), displayName));
                if (!added) {
                    Toast.makeText(this, R.string.error_photo_duplicate, Toast.LENGTH_SHORT).show();
                    return;
                }

                persistAndRefresh();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        appData = DataStore.load(this);
        albumIndex = getIntent().getIntExtra(EXTRA_ALBUM_INDEX, -1);

        if (albumIndex < 0 || albumIndex >= appData.getAlbums().size()) {
            Toast.makeText(this, R.string.error_album_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        album = appData.getAlbums().get(albumIndex);

        albumTitle = findViewById(R.id.text_album_title);
        Button addPhotoButton = findViewById(R.id.button_add_photo);
        ListView photoListView = findViewById(R.id.list_photos);
        TextView emptyView = findViewById(R.id.text_empty_photos);

        photoListAdapter = new PhotoListAdapter(this, photoItems);
        photoListView.setAdapter(photoListAdapter);
        photoListView.setEmptyView(emptyView);

        addPhotoButton.setOnClickListener(view -> launchPhotoPicker());
        photoListView.setOnItemClickListener((parent, view, position, id) -> openPhoto(position));
        photoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showRemovePhotoDialog(position);
            return true;
        });

        refreshPhotoList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appData = DataStore.load(this);
        if (albumIndex < 0 || albumIndex >= appData.getAlbums().size()) {
            Toast.makeText(this, R.string.error_album_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        album = appData.getAlbums().get(albumIndex);
        refreshPhotoList();
    }

    private void refreshPhotoList() {
        albumTitle.setText(album.getName());
        photoItems.clear();
        photoItems.addAll(album.getPhotos());
        photoListAdapter.notifyDataSetChanged();
    }

    private void launchPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickPhotoLauncher.launch(intent);
    }

    private void showRemovePhotoDialog(int photoIndex) {
        Photo photo = photoItems.get(photoIndex);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.remove_photo)
                .setMessage(getString(R.string.remove_photo_message, photo.getDisplayName()))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    album.removePhotoByUri(photo.getUriString());
                    persistAndRefresh();
                })
                .show();
    }

    private void openPhoto(int photoIndex) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.EXTRA_ALBUM_INDEX, albumIndex);
        intent.putExtra(PhotoActivity.EXTRA_PHOTO_INDEX, photoIndex);
        startActivity(intent);
    }

    private void persistAndRefresh() {
        try {
            DataStore.save(this, appData);
            refreshPhotoList();
        } catch (IllegalStateException exception) {
            Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void tryTakePersistablePermission(Intent resultData, Uri selectedUri) {
        int takeFlags = resultData.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            getContentResolver().takePersistableUriPermission(selectedUri, takeFlags);
        } catch (SecurityException exception) {
            // Some providers do not allow persistable permissions. Read access may still work.
        }
    }

    private String resolveDisplayName(Uri uri) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex >= 0) {
                    String displayName = cursor.getString(columnIndex);
                    if (displayName != null && !displayName.trim().isEmpty()) {
                        return displayName.trim();
                    }
                }
            }
        } catch (Exception exception) {
            // Fall back to URI parsing below.
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        String lastSegment = uri.getLastPathSegment();
        if (lastSegment == null || lastSegment.trim().isEmpty()) {
            return getString(R.string.photo_default_name);
        }
        return lastSegment;
    }
}
