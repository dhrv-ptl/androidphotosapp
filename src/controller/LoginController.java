package controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Controller for the login view.
 *
 * @author Dhruv Patel
 */
public class LoginController {

    private Stage stage;

    /**
     * Stores the primary stage reference.
     *
     * @param stage primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        // Placeholder for future setup.
    }

    /**
     * Returns the current stage reference.
     *
     * @return primary stage
     */
    public Stage getStage() {
        return stage;
    }
}
