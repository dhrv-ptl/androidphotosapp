package app;

import controller.AdminController;
import controller.AlbumController;
import controller.SearchController;
import controller.LoginController;
import controller.UserAlbumsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Album;
import model.PhotosData;
import model.User;
import util.DataStore;

import java.io.IOException;

/**
 * Main JavaFX launcher for the PhotosXX application.
 *
 * @author Dhruv Patel
 */
public class Photos extends Application {

    private PhotosData data;
    private Stage primaryStage;

    /**
     * Starts the application and opens the login screen.
     *
     * @param stage primary stage
     * @throws IOException if the login view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        data = DataStore.load();
        primaryStage = stage;
        primaryStage.setTitle("PhotosXX");
        showLoginView();
        primaryStage.show();
    }

    /**
     * Saves application data on exit.
     */
    @Override
    public void stop() {
        if (data != null) {
            try {
                DataStore.save(data);
            } catch (IllegalStateException exception) {
                // Exit-time save failure cannot be recovered here without broader UI changes.
            }
        }
    }

    /**
     * Returns the application data.
     *
     * @return application data
     */
    public PhotosData getData() {
        return data;
    }

    /**
     * Saves application data and shows a GUI error if saving fails.
     *
     * @return true if save succeeded
     */
    public boolean saveData() {
        try {
            DataStore.save(data);
            return true;
        } catch (IllegalStateException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Save Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to save application data.");
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Shows the login screen.
     *
     * @throws IOException if the view cannot be loaded
     */
    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/login-view.fxml"));
        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setApp(this);
        controller.setData(data);

        primaryStage.setScene(new Scene(root));
    }

    /**
     * Shows the admin screen.
     *
     * @throws IOException if the view cannot be loaded
     */
    public void showAdminView() throws IOException {
        FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/admin-view.fxml"));
        Parent root = loader.load();

        AdminController controller = loader.getController();
        controller.setApp(this);
        controller.setData(data);

        primaryStage.setScene(new Scene(root));
    }

    /**
     * Shows the user albums screen.
     *
     * @param user logged-in user
     * @throws IOException if the view cannot be loaded
     */
    public void showUserAlbumsView(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/user-albums-view.fxml"));
        Parent root = loader.load();

        UserAlbumsController controller = loader.getController();
        controller.setApp(this);
        controller.setData(data);
        controller.setUser(user);

        primaryStage.setScene(new Scene(root));
    }

    /**
     * Shows the album screen.
     *
     * @param user current user
     * @param album selected album
     * @throws IOException if the view cannot be loaded
     */
    public void showAlbumView(User user, Album album) throws IOException {
        FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/album-view.fxml"));
        Parent root = loader.load();

        AlbumController controller = loader.getController();
        controller.setApp(this);
        controller.setData(data);
        controller.setUser(user);
        controller.setAlbum(album);

        primaryStage.setScene(new Scene(root));
    }

    /**
     * Shows the search screen.
     *
     * @param user current user
     * @throws IOException if the view cannot be loaded
     */
    public void showSearchView(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/search-view.fxml"));
        Parent root = loader.load();

        SearchController controller = loader.getController();
        controller.setApp(this);
        controller.setData(data);
        controller.setUser(user);

        primaryStage.setScene(new Scene(root));
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
