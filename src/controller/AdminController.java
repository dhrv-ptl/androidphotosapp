package controller;

import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import model.PhotosData;

import java.io.IOException;

/**
 * Controller for the admin view.
 *
 * @author Dhruv Patel
 */
public class AdminController {

    @FXML
    private Label messageLabel;

    private Photos app;
    private PhotosData data;

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
        if (messageLabel != null) {
            messageLabel.setText("Admin user: admin");
        }
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        if (data != null) {
            messageLabel.setText("Admin user: admin");
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
