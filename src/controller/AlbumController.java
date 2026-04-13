package controller;

import app.Photos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Album;
import model.Photo;
import model.PhotosData;
import model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller for the album view.
 *
 * @author Dhruv Patel
 */
public class AlbumController {

    @FXML
    private Label albumLabel;

    @FXML
    private ListView<Photo> photoListView;

    @FXML
    private ImageView previewImageView;

    @FXML
    private Label captionLabel;

    @FXML
    private Label pathLabel;

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
        refreshPhotoList(null);
    }

    /**
     * Initializes the view.
     */
    @FXML
    private void initialize() {
        photoListView.setCellFactory(listView -> new ListCell<>() {
            private final ImageView thumbnailView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label cellCaptionLabel = new Label();
            private final VBox textBox = new VBox(4);
            private final HBox content = new HBox(10);

            {
                thumbnailView.setFitWidth(60);
                thumbnailView.setFitHeight(60);
                thumbnailView.setPreserveRatio(true);
                textBox.getChildren().addAll(nameLabel, cellCaptionLabel);
                content.getChildren().addAll(thumbnailView, textBox);
            }

            @Override
            protected void updateItem(Photo photo, boolean empty) {
                super.updateItem(photo, empty);
                if (empty || photo == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(Path.of(photo.getAbsoluteFilePath()).getFileName().toString());
                String caption = photo.getCaption().isBlank() ? "(No caption)" : photo.getCaption();
                cellCaptionLabel.setText(caption);
                thumbnailView.setImage(loadImage(photo, 60, 60));
                setText(null);
                setGraphic(content);
            }
        });

        photoListView.getSelectionModel().selectedItemProperty().addListener((observable, oldPhoto, newPhoto) ->
                updateSelectedPhotoDetails(newPhoto));
        updateMessage();
        updateSelectedPhotoDetails(null);
    }

    /**
     * Adds a photo to the album from the local computer.
     */
    @FXML
    private void handleAddPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image Files", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png"));

        File selectedFile = fileChooser.showOpenDialog(photoListView.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.toPath().toAbsolutePath().normalize().toString();
        if (!isSupportedImageFile(absolutePath)) {
            showError("Please choose a bmp, gif, jpg, jpeg, or png image.");
            return;
        }
        if (album.containsPhoto(absolutePath)) {
            showError("That photo is already in this album.");
            return;
        }

        try {
            user.addPhotoToAlbum(album.getName(), absolutePath);
            refreshPhotoList(absolutePath);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Removes the selected photo from this album.
     */
    @FXML
    private void handleRemovePhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to remove.");
            return;
        }

        boolean removed = user.removePhotoFromAlbum(album.getName(), selectedPhoto.getAbsoluteFilePath());
        if (!removed) {
            showError("Unable to remove the selected photo.");
            return;
        }

        refreshPhotoList(null);
    }

    /**
     * Updates the caption for the selected photo.
     */
    @FXML
    private void handleRecaptionPhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to recaption.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
        dialog.setTitle("Recaption Photo");
        dialog.setHeaderText(null);
        dialog.setContentText("Caption:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        selectedPhoto.setCaption(result.get());
        refreshPhotoList(selectedPhoto.getAbsoluteFilePath());
    }

    /**
     * Shows the previous photo in the album.
     */
    @FXML
    private void handlePreviousPhoto() {
        selectAdjacentPhoto(-1);
    }

    /**
     * Shows the next photo in the album.
     */
    @FXML
    private void handleNextPhoto() {
        selectAdjacentPhoto(1);
    }

    /**
     * Copies the selected photo to another album.
     */
    @FXML
    private void handleCopyPhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to copy.");
            return;
        }

        Album destinationAlbum = chooseDestinationAlbum();
        if (destinationAlbum == null) {
            return;
        }

        String absolutePath = selectedPhoto.getAbsoluteFilePath();
        if (destinationAlbum.containsPhoto(absolutePath)) {
            showError("That photo is already in the destination album.");
            return;
        }

        try {
            user.addPhotoToAlbum(destinationAlbum.getName(), absolutePath);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    /**
     * Moves the selected photo to another album.
     */
    @FXML
    private void handleMovePhoto() {
        Photo selectedPhoto = getSelectedPhoto();
        if (selectedPhoto == null) {
            showError("Please select a photo to move.");
            return;
        }

        Album destinationAlbum = chooseDestinationAlbum();
        if (destinationAlbum == null) {
            return;
        }

        String absolutePath = selectedPhoto.getAbsoluteFilePath();
        if (destinationAlbum.containsPhoto(absolutePath)) {
            showError("That photo is already in the destination album.");
            return;
        }

        try {
            user.addPhotoToAlbum(destinationAlbum.getName(), absolutePath);
            user.removePhotoFromAlbum(album.getName(), absolutePath);
            refreshPhotoList(null);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
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

    private void refreshPhotoList(String photoPathToSelect) {
        if (photoListView == null || album == null) {
            return;
        }

        photoListView.setItems(FXCollections.observableArrayList(album.getPhotos()));
        if (photoPathToSelect != null) {
            Photo photo = album.getPhotoByPath(photoPathToSelect);
            if (photo != null) {
                photoListView.getSelectionModel().select(photo);
                return;
            }
        }

        if (!photoListView.getItems().isEmpty()) {
            photoListView.getSelectionModel().selectFirst();
        } else {
            updateSelectedPhotoDetails(null);
        }
    }

    private void updateSelectedPhotoDetails(Photo photo) {
        if (previewImageView == null || captionLabel == null || pathLabel == null) {
            return;
        }

        if (photo == null) {
            previewImageView.setImage(null);
            captionLabel.setText("Caption: ");
            pathLabel.setText("Path: ");
            return;
        }

        previewImageView.setImage(loadImage(photo, 420, 320));
        String caption = photo.getCaption().isBlank() ? "(No caption)" : photo.getCaption();
        captionLabel.setText("Caption: " + caption);
        pathLabel.setText("Path: " + photo.getAbsoluteFilePath());
    }

    private Photo getSelectedPhoto() {
        return photoListView.getSelectionModel().getSelectedItem();
    }

    private void selectAdjacentPhoto(int offset) {
        if (photoListView.getItems().isEmpty()) {
            showError("There are no photos in this album.");
            return;
        }

        int currentIndex = photoListView.getSelectionModel().getSelectedIndex();
        if (currentIndex < 0) {
            photoListView.getSelectionModel().selectFirst();
            return;
        }

        int nextIndex = currentIndex + offset;
        if (nextIndex < 0 || nextIndex >= photoListView.getItems().size()) {
            showError("No more photos in that direction.");
            return;
        }

        photoListView.getSelectionModel().select(nextIndex);
        photoListView.scrollTo(nextIndex);
    }

    private Album chooseDestinationAlbum() {
        if (user == null || album == null) {
            return null;
        }

        List<Album> destinationAlbums = new ArrayList<>();
        for (Album userAlbum : user.getAlbums()) {
            if (!userAlbum.getName().equals(album.getName())) {
                destinationAlbums.add(userAlbum);
            }
        }

        if (destinationAlbums.isEmpty()) {
            showError("Create another album first.");
            return null;
        }

        ChoiceDialog<Album> dialog = new ChoiceDialog<>(destinationAlbums.getFirst(), destinationAlbums);
        dialog.setTitle("Choose Album");
        dialog.setHeaderText(null);
        dialog.setContentText("Destination album:");

        Optional<Album> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private Image loadImage(Photo photo, double width, double height) {
        Image image = new Image(Path.of(photo.getAbsoluteFilePath()).toUri().toString(),
                width, height, true, true, false);
        if (image.isError()) {
            return null;
        }
        return image;
    }

    private boolean isSupportedImageFile(String absolutePath) {
        String lowerCasePath = absolutePath.toLowerCase(Locale.ROOT);
        return lowerCasePath.endsWith(".bmp")
                || lowerCasePath.endsWith(".gif")
                || lowerCasePath.endsWith(".jpg")
                || lowerCasePath.endsWith(".jpeg")
                || lowerCasePath.endsWith(".png");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Album Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
