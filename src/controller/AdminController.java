package controller;

import app.Photos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import model.PhotosData;
import model.User;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the admin view.
 *
 * @author Dhruv Patel
 */
public class AdminController {

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<String> userListView;

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
        refreshUserList();
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        messageLabel.setText("Admin user: admin");
        refreshUserList();
    }

    /**
     * Creates a new user.
     */
    @FXML
    private void handleCreateUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create User");
        dialog.setHeaderText(null);
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String username = result.get().trim();
        if (username.isEmpty()) {
            showError("Please enter a username.");
            return;
        }

        if ("admin".equals(username) || data.hasUser(username)) {
            showError("That username already exists.");
            return;
        }

        try {
            data.addUser(username);
            refreshUserList();
            userListView.getSelectionModel().select(username);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Deletes the selected user.
     */
    @FXML
    private void handleDeleteUser() {
        String selectedUsername = userListView.getSelectionModel().getSelectedItem();
        if (selectedUsername == null) {
            showError("Please select a user to delete.");
            return;
        }

        if ("admin".equals(selectedUsername)) {
            showError("The admin user cannot be deleted here.");
            return;
        }

        boolean removed = data.removeUser(selectedUsername);
        if (!removed) {
            showError("Unable to delete the selected user.");
            return;
        }

        refreshUserList();
    }

    /**
     * Logs out and returns to the login screen.
     */
    @FXML
    private void handleLogout() {
        if (!app.saveData()) {
            return;
        }
        try {
            app.showLoginView();
        } catch (IOException exception) {
            showError("Unable to return to the login screen.");
        }
    }

    private void refreshUserList() {
        if (userListView == null || data == null) {
            return;
        }

        userListView.setItems(FXCollections.observableArrayList(
                data.getUsers().stream()
                        .map(User::getUsername)
                        .toList()
        ));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
