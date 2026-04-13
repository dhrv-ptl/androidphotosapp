package app;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.PhotosData;
import util.DataStore;

import java.io.IOException;

/**
 * Main JavaFX launcher for the PhotosXX application.
 *
 * @author Dhruv Patel
 */
public class Photos extends Application {

    private PhotosData data;

    /**
     * Starts the application and opens the login screen.
     *
     * @param stage primary stage
     * @throws IOException if the login view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        data = DataStore.load();

        FXMLLoader loader = new FXMLLoader(Photos.class.getResource("/view/login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.setStage(stage);

        stage.setTitle("PhotosXX");
        stage.setScene(scene);
        stage.show();
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
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
