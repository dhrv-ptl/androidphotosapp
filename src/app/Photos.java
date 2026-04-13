package app;

import controller.AdminController;
import controller.LoginController;
import controller.UserAlbumsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
            DataStore.save(data);
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
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
