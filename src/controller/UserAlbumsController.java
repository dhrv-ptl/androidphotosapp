package controller;

import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import model.PhotosData;
import model.User;

import java.io.IOException;

/**
 * Controller for the user albums view.
 *
 * @author Dhruv Patel
 */
public class UserAlbumsController {

    @FXML
    private Label messageLabel;

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
        updateMessage();
    }

    /**
     * Stores the logged-in user.
     *
     * @param user logged-in user
     */
    public void setUser(User user) {
        this.user = user;
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
