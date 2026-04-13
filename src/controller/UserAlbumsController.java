package controller;

import app.Photos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import model.Album;
import model.PhotosData;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller for the user albums view.
 *
 * @author Dhruv Patel
 */
public class UserAlbumsController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<Album> albumListView;

    private Photos app;
    private PhotosData data;
    private User user;

    /**
     * Stores the application reference.
     *
     * @param app application
     */
    public void setApp(Photos app) {
        this.app = app;
    }

    /**
     * Stores the application data.
     *
     * @param data application data
     */
    public void setData(PhotosData data) {
        this.data = data;
        refreshAlbumList();
    }

    /**
     * Stores the logged-in user.
     *
     * @param user logged-in user
     */
    public void setUser(User user) {
        this.user = user;
        updateMessage();
        refreshAlbumList();
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        albumListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Album album, boolean empty) {
                super.updateItem(album, empty);
                if (empty || album == null) {
                    setText(null);
                    return;
                }

                setText(album.getName()
                        + "\nPhotos: " + album.getPhotoCount()
                        + "\nEarliest: " + formatDate(album.getEarliestPhotoDate().orElse(null))
                        + "\nLatest: " + formatDate(album.getLatestPhotoDate().orElse(null)));
            }
        });
        updateMessage();
        refreshAlbumList();
    }

    /**
     * Creates an album.
     */
    @FXML
    private void handleCreateAlbum() {
        String albumName = promptForAlbumName("Create Album", null);
        if (albumName == null) {
            return;
        }

        try {
            user.createAlbum(albumName);
            refreshAlbumList();
            selectAlbumByName(albumName);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Deletes the selected album.
     */
    @FXML
    private void handleDeleteAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showError("Please select an album to delete.");
            return;
        }

        boolean removed = user.deleteAlbum(selectedAlbum.getName());
        if (!removed) {
            showError("Unable to delete the selected album.");
            return;
        }

        refreshAlbumList();
    }

    /**
     * Renames the selected album.
     */
    @FXML
    private void handleRenameAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showError("Please select an album to rename.");
            return;
        }

        String newAlbumName = promptForAlbumName("Rename Album", selectedAlbum.getName());
        if (newAlbumName == null) {
            return;
        }

        try {
            boolean renamed = user.renameAlbum(selectedAlbum.getName(), newAlbumName);
            if (!renamed) {
                showError("Unable to rename the selected album.");
                return;
            }
            refreshAlbumList();
            selectAlbumByName(newAlbumName);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Opens the selected album screen.
     */
    @FXML
    private void handleOpenAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showError("Please select an album to open.");
            return;
        }

        try {
            app.showAlbumView(user, selectedAlbum);
        } catch (IOException exception) {
            showError("Unable to open the selected album.");
        }
    }

    /**
     * Logs out and returns to the login screen.
     */
    @FXML
    private void handleLogout() {
        try {
            app.showLoginView();
        } catch (IOException exception) {
            showError("Unable to return to the login screen.");
        }
    }

    private void updateMessage() {
        if (messageLabel != null && user != null) {
            messageLabel.setText("Logged in as: " + user.getUsername());
        }
    }

    private void refreshAlbumList() {
        if (albumListView == null || user == null) {
            return;
        }

        albumListView.setItems(FXCollections.observableArrayList(user.getAlbums()));
    }

    private String promptForAlbumName(String title, String currentName) {
        TextInputDialog dialog = new TextInputDialog(currentName == null ? "" : currentName);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText("Album name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return null;
        }

        String albumName = result.get().trim();
        if (albumName.isEmpty()) {
            showError("Please enter an album name.");
            return null;
        }
        return albumName;
    }

    private void selectAlbumByName(String albumName) {
        Album album = user.getAlbum(albumName);
        if (album != null) {
            albumListView.getSelectionModel().select(album);
        }
    }

    private String formatDate(LocalDate date) {
        return date == null ? "N/A" : DATE_FORMATTER.format(date);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Album Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
