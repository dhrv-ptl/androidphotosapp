package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the create album from search dialog view.
 *
 * @author Dhruv Patel
 */
public class CreateAlbumFromSearchDialogController {

    @FXML
    private TextField albumNameField;

    private Stage stage;
    private String albumName;

    /**
     * Stores the dialog stage.
     *
     * @param stage dialog stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Returns the entered album name.
     *
     * @return album name or null
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Creates the album request and closes the dialog.
     */
    @FXML
    private void handleCreate() {
        albumName = albumNameField.getText() == null ? "" : albumNameField.getText().trim();
        stage.close();
    }

    /**
     * Closes the dialog without creating an album.
     */
    @FXML
    private void handleCancel() {
        albumName = null;
        stage.close();
    }
}
