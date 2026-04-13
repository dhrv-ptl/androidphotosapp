package controller;

import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.PhotosData;
import model.User;

import java.io.IOException;

/**
 * Controller for the login view.
 *
 * @author Dhruv Patel
 */
public class LoginController {

    @FXML
    private TextField usernameField;

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
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        // Placeholder for future setup.
    }

    /**
     * Handles username login.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        if (username.isEmpty()) {
            showError("Please enter a username.");
            return;
        }

        if ("admin".equals(username)) {
            try {
                app.showAdminView();
            } catch (IOException exception) {
                showError("Unable to open the admin screen.");
            }
            return;
        }

        User user = data.getUser(username);
        if (user == null) {
            showError("Invalid username.");
            return;
        }

        try {
            app.showUserAlbumsView(user);
        } catch (IOException exception) {
            showError("Unable to open the user screen.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
