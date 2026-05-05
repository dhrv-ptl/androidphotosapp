package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.androidphotos.storage.DataStore;

/**
 * Home screen that lists albums and supports basic album management.
 */
public class MainActivity extends AppCompatActivity {

    private AppData appData;
    private ArrayAdapter<String> albumAdapter;
    private final List<String> albumNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appData = DataStore.load(this);

        Button createAlbumButton = findViewById(R.id.button_create_album);
        Button searchButton = findViewById(R.id.button_search);
        ListView albumListView = findViewById(R.id.list_albums);
        TextView emptyView = findViewById(R.id.text_empty_albums);

        albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumNames);
        albumListView.setAdapter(albumAdapter);
        albumListView.setEmptyView(emptyView);

        createAlbumButton.setOnClickListener(view -> showCreateAlbumDialog());
        searchButton.setOnClickListener(view -> openSearch());
        albumListView.setOnItemClickListener((parent, view, position, id) -> openAlbum(position));
        albumListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showAlbumOptionsDialog(position);
            return true;
        });

        refreshAlbumList();
    }

    private void refreshAlbumList() {
        albumNames.clear();
        for (Album album : appData.getAlbums()) {
            albumNames.add(album.getName());
        }
        albumAdapter.notifyDataSetChanged();
    }

    private void showCreateAlbumDialog() {
        final EditText input = createAlbumNameInput("");

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.create_album)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.create, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String albumName = input.getText().toString();
                    String validationMessage = validateAlbumName(albumName, null);
                    if (validationMessage != null) {
                        input.setError(validationMessage);
                        return;
                    }

                    appData.addAlbum(albumName.trim());
                    persistAndRefresh();
                    dialog.dismiss();
                }));

        dialog.show();
    }

    private void showAlbumOptionsDialog(int position) {
        final String albumName = albumNames.get(position);
        final String[] options = {
                getString(R.string.rename_album),
                getString(R.string.delete_album)
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle(albumName)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameAlbumDialog(albumName);
                    } else if (which == 1) {
                        showDeleteAlbumDialog(albumName);
                    }
                })
                .show();
    }

    private void showRenameAlbumDialog(String currentAlbumName) {
        final EditText input = createAlbumNameInput(currentAlbumName);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.rename_album)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.rename, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(view -> {
                    String newAlbumName = input.getText().toString();
                    String validationMessage = validateAlbumName(newAlbumName, currentAlbumName);
                    if (validationMessage != null) {
                        input.setError(validationMessage);
                        return;
                    }

                    appData.renameAlbum(currentAlbumName, newAlbumName.trim());
                    persistAndRefresh();
                    dialog.dismiss();
                }));

        dialog.show();
    }

    private void showDeleteAlbumDialog(String albumName) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_album)
                .setMessage(getString(R.string.delete_album_message, albumName))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    appData.deleteAlbum(albumName);
                    persistAndRefresh();
                })
                .show();
    }

    private void openAlbum(int position) {
        String albumName = albumNames.get(position);
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra(AlbumActivity.EXTRA_ALBUM_NAME, albumName);
        intent.putExtra(AlbumActivity.EXTRA_ALBUM_INDEX, position);
        startActivity(intent);
    }

    private void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private EditText createAlbumNameInput(String initialValue) {
        EditText input = new EditText(this);
        input.setHint(R.string.album_name_hint);
        input.setSingleLine(true);
        input.setText(initialValue);
        input.setSelection(input.getText().length());
        return input;
    }

    private String validateAlbumName(String candidateName, String currentAlbumName) {
        if (candidateName == null || candidateName.trim().isEmpty()) {
            return getString(R.string.error_album_blank);
        }

        String trimmedCandidate = candidateName.trim();
        for (Album album : appData.getAlbums()) {
            boolean sameAlbum = currentAlbumName != null
                    && album.getName().equalsIgnoreCase(currentAlbumName);
            if (!sameAlbum && album.getName().equalsIgnoreCase(trimmedCandidate)) {
                return getString(R.string.error_album_duplicate);
            }
        }
        return null;
    }

    private void persistAndRefresh() {
        try {
            DataStore.save(this, appData);
            refreshAlbumList();
        } catch (IllegalStateException exception) {
            Toast.makeText(this, R.string.error_save_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
