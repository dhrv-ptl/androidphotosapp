package controller;

import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import model.Album;
import model.PhotosData;
import model.User;

import java.io.IOException;

/**
 * Controller for the album view.
 *
 * @author Dhruv Patel
 */
public class AlbumController {

    @FXML
    private Label albumLabel;

    private Photos app;
    private PhotosData data;
    private User user;
    private Album album;

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
        updateMessage();
    }

    /**
     * Stores the current user.
     *
     * @param user current user
     */
    public void setUser(User user) {
        this.user = user;
        updateMessage();
    }

    /**
     * Stores the current album.
     *
     * @param album current album
     */
    public void setAlbum(Album album) {
        this.album = album;
        updateMessage();
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        updateMessage();
    }

    /**
     * Returns to the user's album list.
     */
    @FXML
    private void handleBack() {
        try {
            app.showUserAlbumsView(user);
        } catch (IOException exception) {
            showError("Unable to return to the album list.");
        }
    }

    private void updateMessage() {
        if (albumLabel != null && user != null && album != null) {
            albumLabel.setText("Album: " + album.getName() + " (" + user.getUsername() + ")");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Album Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
